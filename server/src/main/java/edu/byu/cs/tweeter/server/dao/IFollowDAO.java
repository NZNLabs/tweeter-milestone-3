package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.model.DBFollow;
import edu.byu.cs.tweeter.server.model.response.DBFollowResponse;

public interface IFollowDAO {
    CountResponse getFollowingCount(String followerAlias);
    CountResponse getFollowerCount(String followeeAlias);
    DBFollowResponse getFollowees(FollowingRequest request);
    DBFollowResponse getFollowers(FollowersRequest request);

    IsFollowerResponse getIsFollower(IsFollowerRequest request);

    Response deleteFollow(DBFollow request);
    Response postFollow(DBFollow request);

}
