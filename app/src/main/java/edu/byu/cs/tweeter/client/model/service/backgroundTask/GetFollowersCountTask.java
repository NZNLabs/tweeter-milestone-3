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
 * Background task that queries how many other users a specified user is followed by.
 */
public class GetFollowersCountTask extends BackgroundTask {
    public static final String COUNT_KEY = "count";
    private static final String LOG_TAG = "GetFollowersCountTask";

    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;

    private String targetUserAlias;

    public GetFollowersCountTask(AuthToken authToken, String targetUserAlias, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.targetUserAlias = targetUserAlias;
    }

    @Override
    protected void runTask() {

        try {


            CountRequest request = new CountRequest(authToken, targetUserAlias);
            CountResponse response = getServerFacade().getFollowerCount(request, FollowService.URL_PATH_GET_FOLLOWER_COUNT);

            if (response.isSuccess()) {
                Integer count = response.getCount();

                Bundle extraBundle = new Bundle();
                extraBundle.putInt(COUNT_KEY, count);
                sendSuccessMessage(extraBundle);
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get follower count", ex);
            sendExceptionMessage(ex);
        }


    }
}
