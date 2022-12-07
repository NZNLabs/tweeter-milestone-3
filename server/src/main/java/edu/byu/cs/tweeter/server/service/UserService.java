package edu.byu.cs.tweeter.server.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.model.DBAuthToken;
import edu.byu.cs.tweeter.server.model.DBFollow;
import edu.byu.cs.tweeter.server.util.AuthManagement;
import edu.byu.cs.tweeter.server.util.Constants;
import edu.byu.cs.tweeter.server.util.PBKDF2WithHmacSHA1Hashing;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class UserService extends AbstractService {

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        DBAuthToken dbAuthToken = getAuthDAO().getToken(request.getUsername());
        boolean passwordMatch;
        try {
            assert dbAuthToken != null;
            passwordMatch = PBKDF2WithHmacSHA1Hashing.validatePassword(request.getPassword(), dbAuthToken.password);
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResponse("Error: Unable to find auth token: " + e.getClass());
        }

        if (!passwordMatch) {
            return new LoginResponse("Invalid Password");
        }

        // updating expiration date on auth token
        boolean success = AuthManagement.updateAuthToken(dbAuthToken, getAuthDAO());
        if (!success) {
            String error = "Login: Unable to update auth token";
            System.out.println(error);
            return new LoginResponse(error);
        }

        AuthToken authToken = new AuthToken(dbAuthToken.authToken, dbAuthToken.dateTime);
        UserResponse userResponse = getUserDAO().getUser(request.getUsername());
        if (userResponse.isSuccess()) {

            return new LoginResponse(userResponse.getUser(), authToken);
        } else {
            return new LoginResponse("Error: User doesn't exist" + userResponse.getMessage());
        }
    }

    public Response logout(AuthToken request) {
        if(request == null){
            throw new RuntimeException("[Bad Request] Missing auth token");
        }
        return new Response(true);
    }

    public LoginResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        } else if(request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        } else if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        }

        // creating auth token
        DBAuthToken authToken;
        try {
            authToken = new DBAuthToken(
                    createAuthToken(),
                    String.valueOf(System.currentTimeMillis()),
                    PBKDF2WithHmacSHA1Hashing.generateStrongPasswordHash(request.getPassword()),
                    request.getUsername()
            );
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return new LoginResponse("Couln't hash password");
        }

        boolean success = getAuthDAO().postToken(authToken);
        if (!success) { return new LoginResponse("Unable to authenticate user"); }

        // Create AmazonS3 object for doing S3 operations
        String resourceURL = null;
        try {
            // https://jacob-cs340-aws-s3.s3.us-west-2.amazonaws.com/profile-pic/jacob.zinn.txt
            // https://s3.us-west-2.amazonaws.com/jacob-cs340-aws-s3/profile-pic/jacob.zinn.txt

            S3Client s3Client = S3Client.builder().region(Region.US_WEST_2).build();
            String bucket_name = Constants.S3_BUCKET_ID;
            String key = "profile-pic/" + request.getUsername().replace("@","") + ".jpg";

            byte[] imageBytes = Base64.getDecoder().decode(request.getImage());

            PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket_name).key(key).build(); // acl("public-read")
            s3Client.putObject(objectRequest, RequestBody.fromBytes(imageBytes));

            GetUrlRequest urlRequest = GetUrlRequest.builder().bucket(bucket_name).key(key).build();
            resourceURL = s3Client.utilities().getUrl(urlRequest).toExternalForm();
        } catch (Exception e) {
            System.out.println("FAILED TO POST IMAGE");
            System.out.println(e.getClass());
            e.printStackTrace();
        }

        User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), resourceURL);
        boolean postUserSuccess = getUserDAO().postUser(user);
        populateDBWithUsers(user, getUserDAO(), daoFactory.getFollowDAO());

        if (postUserSuccess) {
            return new LoginResponse(user, new AuthToken(authToken.authToken, authToken.dateTime));
        } else {
            return new LoginResponse("Error: Failed to post user");
        }
    }

    private void populateDBWithUsers(User newUser, IUserDAO userDAO, IFollowDAO followDAO) {
        try {
            System.out.println("populateDBWithUsers: populating users");
            ArrayList<User> users = new ArrayList<>(FakeData.getInstance().getFakeUsers());
            for (User user : users) {
                userDAO.postUser(user);
            }

            users.add(newUser); // ensuring that the person being registered gets followers ;)

            final int numFollowers = 3;
            ArrayList<DBFollow> randomFollows = new ArrayList<>();
            for (User user : users) {

                // find N random followers for every user
                Random random = new Random();
                ArrayList<Integer> randomIndices = new ArrayList<>();
                while (randomIndices.size() < numFollowers) {
                    int idx = random.nextInt(users.size());
                    boolean idxNotBeingUsed = true;
                    for (Integer index : randomIndices) {
                        if (idx == index) {
                            idxNotBeingUsed = false;
                            break;
                        }
                    }
                    if (idxNotBeingUsed) {
                        randomIndices.add(idx);
                    }
                }

                // create the follow list
                for (Integer idx : randomIndices) {
                    User follower = users.get(idx);
                    randomFollows.add(new DBFollow(follower.getAlias(), follower.getName(), user.getAlias(), user.getName()));
                }
            }

            System.out.println("populateDBWithUsers: posting follows");
            // post the follow list to db
            for (DBFollow follow : randomFollows) {
                followDAO.postFollow(follow);
            }

            System.out.println("populateDBWithUsers: success");
        } catch (Exception e) {
            System.out.println("populateDBWithUsers: exception: " + e.getClass());
            e.printStackTrace();
        }
    }


    private String createAuthToken() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }

    public UserResponse getUser(UserRequest request) {
        if(request.getUserAlias() == null){
            throw new RuntimeException("[Bad Request] Missing an alias");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Missing auth token");
        }

        boolean isValid = AuthManagement.validateAuthToken(request.getAuthToken(), getAuthDAO());
        if (!isValid) { return new UserResponse("expired"); }

        return getUserDAO().getUser(request.getUserAlias());
    }

}
