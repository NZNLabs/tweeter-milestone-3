package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends BackgroundTask {
    private static final String LOG_TAG = "GetUserTask";

    public static final String USER_KEY = "user";

    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;
    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private String alias;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.alias = alias;
        this.messageHandler = messageHandler;
    }

    @Override
    protected void runTask() {
        User user = getUser();

        Bundle msgBundle = new Bundle();
        msgBundle.putSerializable(USER_KEY, user);
        sendSuccessMessage(msgBundle);
    }

    private FakeData getFakeData() {
        return FakeData.getInstance();
    }

    private User getUser() {
        User user = getFakeData().findUserByAlias(alias);
        return user;
    }
}
