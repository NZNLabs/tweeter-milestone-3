package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.Response;

/**
 * Background task that establishes a following relationship between two users.
 */
public class UnfollowTask extends BackgroundTask {
    private static final String LOG_TAG = "FollowTask";

    /**
     * Auth token for logged-in user.
     * This user is the "follower" in the relationship.
     */
    private AuthToken authToken;
    /**
     * The user that is being followed.
     */
    private User followee;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.followee = followee;
    }

    @Override
    protected void runTask() {

        try {
            String authUserAlias = Cache.getInstance().getCurrUser().getAlias();
            String targetUserAlias = followee == null ? null : followee.getAlias();
            if (targetUserAlias == null) throw new IOException("target user alias must not be null during follow");

            UnfollowRequest request = new UnfollowRequest(authToken, authUserAlias, targetUserAlias);

            Response response = getServerFacade().getUnfollow(request, FollowService.URL_PATH_GET_UNFOLLOW);

            if (response.isSuccess()) {
                sendSuccessMessage(new Bundle());
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to follow", ex);
            sendExceptionMessage(ex);
        }

    }
}
