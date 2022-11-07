package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.utils.GenericMessageHandler;
import edu.byu.cs.tweeter.client.utils.TaskObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    public static final String URL_PATH_GET_FOLLOWING = "/getFollowing";
    public static final String URL_PATH_GET_FOLLOWERS = "/getFollowers";
    public static final String URL_PATH_GET_FOLLOWING_COUNT = "/getFollowingCount";
    public static final String URL_PATH_GET_FOLLOWER_COUNT = "/getFollowerCount";

    /**
     * Creates an instance.
     */
    public FollowService() {}


    // FOLLOWING
    public void getFollowing(AuthToken authToken, User targetUser, int limit, User lastFollowee, TaskObserverInterface observer) {
        GetFollowingTask followingTask = getGetFollowingTask(authToken, targetUser, limit, lastFollowee, observer);
        BackgroundTaskUtils.runTask(followingTask);
    }
    // This method is public so it can be accessed by test cases
    public GetFollowingTask getGetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee, TaskObserverInterface observer) {
        return new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new GenericMessageHandler<>(observer));
    }

    // FOLLOWERS
    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, TaskObserverInterface observer) {
        GetFollowersTask followersTask = getGetFollowersTask(authToken, targetUser, limit, lastFollower, observer);
        BackgroundTaskUtils.runTask(followersTask);
    }

    // This method is public so it can be accessed by test cases
    public GetFollowersTask getGetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower, TaskObserverInterface observer) {
        return new GetFollowersTask(authToken, targetUser, limit, lastFollower, new GenericMessageHandler<>(observer));
    }

    // IS FOLLOW
    public void isFollow(AuthToken authToken, User follower, User followee, TaskObserverInterface observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken, follower, followee, new GenericMessageHandler<>(observer));
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    // POST UNFOLLOW
    public void postUnfollow(AuthToken authToken, User followee, TaskObserverInterface observer) {
        UnfollowTask task = new UnfollowTask(authToken, followee, new GenericMessageHandler<>(observer));
        BackgroundTaskUtils.runTask(task);
    }

    // POST FOLLOW
    public void postFollow(AuthToken authToken, User followee, TaskObserverInterface observer) {
        FollowTask task = new FollowTask(authToken, followee, new GenericMessageHandler<>(observer));
        BackgroundTaskUtils.runTask(task);
    }

    // FOLLOWING COUNT
    public void getFollowingCountObserver(AuthToken authToken, User targetUser, TaskObserverInterface observer) {
        GetFollowingCountTask task = new GetFollowingCountTask(authToken, targetUser.getAlias(), new GenericMessageHandler<>(observer));
        BackgroundTaskUtils.runTask(task);
    }

    // FOLLOWER COUNT
    public void getFollowerCountObserver(AuthToken authToken, User targetUser, TaskObserverInterface observer) {
        GetFollowersCountTask task = new GetFollowersCountTask(authToken, targetUser.getAlias(), new GenericMessageHandler<>(observer));
        BackgroundTaskUtils.runTask(task);
    }

}
