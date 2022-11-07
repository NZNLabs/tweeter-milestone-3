package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends BackgroundTask {
    private static final String LOG_TAG = "IsFollowerTask";

    public static final String IS_FOLLOWER_KEY = "is-follower";

    /**
     * Auth token for logged-in user.
     */
    private final AuthToken authToken;
    /**
     * The alleged follower.
     */
    private final User follower;
    /**
     * The alleged followee.
     */
    private final User followee;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected void runTask() {

        try {

            String followerAlias = follower == null ? null : follower.getAlias();
            String followeeAlias = followee == null ? null : followee.getAlias();

            IsFollowerRequest request = new IsFollowerRequest(authToken, followeeAlias, followerAlias);
            IsFollowerResponse response = getServerFacade().getIsFollower(request, FollowService.URL_PATH_GET_IS_FOLLOW);

            if (response.isSuccess()) {
                boolean isFollower = response.getIsFollower();

                Bundle extraBundle = new Bundle();
                extraBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
                sendSuccessMessage(extraBundle);
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get isFollow", ex);
            sendExceptionMessage(ex);
        }

    }

}
