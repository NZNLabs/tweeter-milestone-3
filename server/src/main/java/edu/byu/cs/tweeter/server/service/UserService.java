package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.IAuthDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.model.DBAuthToken;
import edu.byu.cs.tweeter.server.util.Constants;
import edu.byu.cs.tweeter.server.util.PBKDF2WithHmacSHA1Hashing;

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

        AuthToken authToken = new AuthToken(dbAuthToken.authToken, dbAuthToken.dateTime);
        UserResponse userResponse = getUserDAO().getUser(new UserRequest(authToken, request.getUsername()));
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
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("us-west-2").build();
            String bucket_name = Constants.S3_BUCKET_ID;
            String key = "profile-pic/" + request.getUsername().replace("@","") + ".txt";
            s3Client.putObject(bucket_name, key, request.getImage());
            resourceURL = s3Client.getUrl(Constants.S3_BUCKET_ID, key).toString();
        } catch (Exception e) {
            System.out.println(e.getClass());
        }

        User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), resourceURL);
        boolean postUserSuccess = getUserDAO().postUser(user);
        if (postUserSuccess) {
            return new LoginResponse(user, new AuthToken(authToken.authToken, authToken.dateTime));
        } else {
            return new LoginResponse("Error: Failed to post user");
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

        return getUserDAO().getUser(request);
    }

    IUserDAO getUserDAO() {
        return daoFactory.getUserDAO();
    }

    IAuthDAO getAuthDAO() {return daoFactory.getAuthDAO(); }

}
