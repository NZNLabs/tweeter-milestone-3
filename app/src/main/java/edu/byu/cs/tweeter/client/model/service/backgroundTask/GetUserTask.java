package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends BackgroundTask {
    private static final String LOG_TAG = "GetUserTask";
    public static final String USER_KEY = "user";

    /**
     * Auth token for logged-in user.
     */
    private final AuthToken authToken;
    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private final String alias;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.alias = alias;
    }

    @Override
    protected void runTask() {
        try {
            UserRequest request = new UserRequest(authToken, alias);
            UserResponse response = getServerFacade().getUser(request, UserService.URL_PATH_GET_USER);

            if (response.isSuccess()) {
                Bundle msgBundle = new Bundle();
                msgBundle.putSerializable(USER_KEY, response.getUser());
                sendSuccessMessage(msgBundle);
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }
}
