package edu.byu.cs.tweeter.server.service;

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
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IFollowerDAO;

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
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFollowingDAO().getFollowees(request);
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    IFollowDAO getFollowingDAO() {return daoFactory.getFollowDAO();}


    public CountResponse getFollowingCount(CountRequest request) {
        if(request.getUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include user alias");}
        return getFollowingDAO().getFollowingCount(request.getUserAlias());
    }

    public Response getFollow(FollowRequest request) {
        if(request.getUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include user alias");}
        if(request.getTargetUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include target user alias");}
        return getFollowingDAO().getFollow(request);
    }

    public Response getUnfollow(UnfollowRequest request) {
        if(request.getUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include user alias");}
        if(request.getTargetUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include target user alias");}
        return getFollowingDAO().getUnfollow(request);
    }

    // FOLLOWERS

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFollowersDAO().getFollowers(request);
    }

    IFollowerDAO getFollowersDAO() {return daoFactory.getFollowerDAO();}

    public CountResponse getFollowerCount(CountRequest request) {
        if(request.getUserAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include user alias");}
        return getFollowersDAO().getFollowerCount(request.getUserAlias());
    }

    public IsFollowerResponse getIsFollower (IsFollowerRequest request) {
        if(request.getFollowerAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include follower alias");}
        if(request.getFolloweeAlias() == null) {throw new RuntimeException("[Bad Request] Request needs to include followee alias");}
        return getFollowersDAO().getIsFollower(request);
    }

}
