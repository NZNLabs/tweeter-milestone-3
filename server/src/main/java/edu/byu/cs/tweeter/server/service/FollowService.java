package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IAuthDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.model.DBFollow;
import edu.byu.cs.tweeter.server.model.response.DBFollowResponse;
import edu.byu.cs.tweeter.server.util.AuthManagement;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends AbstractService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        try {

            if (request.getFollowerAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
            } else if (request.getLimit() <= 0) {
                throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
            }

            boolean isValid = AuthManagement.validateAuthToken(request.getAuthToken(), getAuthDAO());
            if (!isValid) { return new FollowingResponse("expired"); }

            DBFollowResponse response = getFollowDAO().getFollowees(request);
            if (!response.isSuccess()) {
                return new FollowingResponse("Failed to get followees");
            }

            ArrayList<User> users = new ArrayList<>();
            for (DBFollow follow : response.getFollows()) {
                User followee = getUserDAO().getUser(follow.followee_handle).getUser();
                if (followee != null) {
                    users.add(followee);
                }
            }

            return new FollowingResponse(users, response.isHasMorePages());
        } catch (Exception e) {
            String error = "Exception: Failed to get followees " + e.getClass();
            System.out.println(error);
            return new FollowingResponse(error);
        }
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        try {

            if(request.getFolloweeAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
            } else if(request.getLimit() <= 0) {
                throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
            }

            AuthToken token = request.getAuthToken();
            System.out.println("TOKEN PRE: dateTime " +token.datetime + " token: " +token.getToken());

            boolean isValid = AuthManagement.validateAuthToken(request.getAuthToken(), getAuthDAO());
            if (!isValid) { return new FollowersResponse("expired"); }

            System.out.println("Get followrs!!");
            DBFollowResponse response = getFollowDAO().getFollowers(request);
            if (!response.isSuccess()) {
                return new FollowersResponse("Failed to get followers");
            }

            ArrayList<User> users = new ArrayList<>();
            for (DBFollow follow : response.getFollows()) {
                User followee = getUserDAO().getUser(follow.follower_handle).getUser();
                if (followee != null) {
                    users.add(followee);
                }
            }

            return new FollowersResponse(users, response.isHasMorePages());
        } catch (Exception e) {
            String error = "Exception: Failed to get followers " + e.getClass();
            System.out.println(error);
            return new FollowersResponse(error);
        }

    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    IFollowDAO getFollowDAO() {return daoFactory.getFollowDAO();}
    IUserDAO getUserDAO() {return daoFactory.getUserDAO();}
    IAuthDAO getAuthDAO() {return daoFactory.getAuthDAO();}


    public CountResponse getFollowingCount(CountRequest request) {
        if(request.getUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include user alias");}

        boolean isValid = AuthManagement.validateAuthToken(request.getAuthToken(), getAuthDAO());
        if (!isValid) {return new CountResponse("expired");}

        return getFollowDAO().getFollowingCount(request.getUserAlias());
    }

    public Response getFollow(FollowRequest request) {
        try {
            if(request.getUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include user alias");}
            if(request.getTargetUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include target user alias");}

            boolean isValid = AuthManagement.validateAuthToken(request.getAuthToken(), getAuthDAO());
            if (!isValid) { return new Response(false, "expired"); }

            User follower = getUserDAO().getUser(request.getUserAlias()).getUser();
            User followee = getUserDAO().getUser(request.getTargetUserAlias()).getUser();
            if (follower == null || followee == null) {
                return new FollowersResponse("getFollow: follwee or follwer alias invalid " + request.getUserAlias() + " " + request.getTargetUserAlias());
            }

            return getFollowDAO().postFollow(new DBFollow(follower.getAlias(), follower.getName(), followee.getAlias(), followee.getName()));
        } catch (Exception e) {
            return new FollowersResponse("Exception: Failed to unfollow: " + e.getClass());
        }
    }

    public Response getUnfollow(UnfollowRequest request) {
        try {
            if(request.getUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include user alias");}
            if(request.getTargetUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include target user alias");}

            boolean isValid = AuthManagement.validateAuthToken(request.getAuthToken(), getAuthDAO());
            if (!isValid) {return new Response(false, "expired");}

            User follower = getUserDAO().getUser(request.getUserAlias()).getUser();
            User followee = getUserDAO().getUser(request.getTargetUserAlias()).getUser();
            if (follower == null || followee == null) {
                return new FollowersResponse("getFollow: follwee or follwer alias invalid " + request.getUserAlias() + " " + request.getTargetUserAlias());
            }

            return getFollowDAO().deleteFollow(new DBFollow(follower.getAlias(), follower.getName(), followee.getAlias(), followee.getName()));
        } catch (Exception e) {
            return new FollowersResponse("Exception: Failed to unfollow: " + e.getClass());
        }
    }

    public CountResponse getFollowerCount(CountRequest request) {
        boolean isValid = AuthManagement.validateAuthToken(request.getAuthToken(), getAuthDAO());
        if (!isValid) {return new CountResponse("expired");}

        if(request.getUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include user alias");}
        return getFollowDAO().getFollowerCount(request.getUserAlias());
    }

    public IsFollowerResponse getIsFollower (IsFollowerRequest request) {
        try {
            if(request.getFollowerAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include follower alias");}
            if(request.getFolloweeAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include followee alias");}

            boolean isValid = AuthManagement.validateAuthToken(request.getAuthToken(), getAuthDAO());
            if (!isValid) { return new IsFollowerResponse("expired"); }

            return getFollowDAO().getIsFollower(request);
        } catch (Exception e) {
            String error = "Exception getIsFollower " + e.getClass();
            System.out.println(error);
            return new IsFollowerResponse(error);
        }
    }

}
