package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

public interface IFollowerDAO {
    CountResponse getFollowerCount(String followerAlias);

    FollowersResponse getFollowers(FollowersRequest request);

    IsFollowerResponse getIsFollower(IsFollowerRequest request);
}
