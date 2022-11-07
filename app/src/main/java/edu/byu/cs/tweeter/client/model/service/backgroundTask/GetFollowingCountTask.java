package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends BackgroundTask {
    public static final String COUNT_KEY = "count";
    private static final String LOG_TAG = "GetFollowingCountTask";

    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;

    private String targetUserAlias;

    public GetFollowingCountTask(AuthToken authToken, String targetUserAlias, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.targetUserAlias = targetUserAlias;
    }

    @Override
    protected void runTask() {

        try {

            CountRequest request = new CountRequest(authToken, targetUserAlias);
            CountResponse response = getServerFacade().getFollowingCount(request, FollowService.URL_PATH_GET_FOLLOWING_COUNT);

            if (response.isSuccess()) {
                Integer count = response.getCount();

                Bundle extraBundle = new Bundle();
                extraBundle.putInt(COUNT_KEY, count);
                sendSuccessMessage(extraBundle);
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get following count", ex);
            sendExceptionMessage(ex);
        }


    }
}
