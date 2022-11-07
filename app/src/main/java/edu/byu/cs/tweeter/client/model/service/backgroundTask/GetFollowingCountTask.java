package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends BackgroundTask {
    public static final String COUNT_KEY = "count";

    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;
    /**
     * The user whose following count is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private User targetUser;

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    @Override
    protected void runTask() {
        int count = 20;
        Bundle extraBundle = new Bundle();
        extraBundle.putInt(COUNT_KEY, count);
        sendSuccessMessage(extraBundle);
    }
}
