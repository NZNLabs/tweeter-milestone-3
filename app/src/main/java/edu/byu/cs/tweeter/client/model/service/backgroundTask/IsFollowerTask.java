package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

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
    private AuthToken authToken;
    /**
     * The alleged follower.
     */
    private User follower;
    /**
     * The alleged followee.
     */
    private User followee;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(IS_FOLLOWER_KEY, new Random().nextInt() > 0);
        sendSuccessMessage(msgBundle);
    }

}
