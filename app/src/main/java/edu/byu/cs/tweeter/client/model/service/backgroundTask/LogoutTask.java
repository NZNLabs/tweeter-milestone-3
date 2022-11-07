package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.response.Response;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends BackgroundTask {
    private static final String LOG_TAG = "LogoutTask";

    /**
     * Auth token for logged-in user.
     */
    private final AuthToken authToken;

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
    }

    @Override
    protected void runTask() {

        try {
            Response response = getServerFacade().logout(authToken, UserService.URL_PATH_LOGOUT);
            if (response.isSuccess()) {
                sendSuccessMessage(new Bundle());
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }

    }
}
