package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.Response;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends BackgroundTask {
    private static final String LOG_TAG = "GetFollowingCountTask";

    /**
     * Auth token for logged-in user.
     */
    private final AuthToken authToken;
    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private final Status status;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.status = status;
    }

    @Override
    protected void runTask() {

        try {

            PostStatusRequest request = new PostStatusRequest(authToken, status);
            Response response = getServerFacade().postStatus(request, StatusService.URL_PATH_POST_STATUS);

            if (response.isSuccess()) {
                sendSuccessMessage(new Bundle());
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to post status", ex);
            sendExceptionMessage(ex);
        }


    }
}
