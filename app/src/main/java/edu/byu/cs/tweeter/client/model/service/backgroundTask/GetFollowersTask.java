package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends BackgroundTask {

    public static final String MORE_PAGES_KEY = "more-pages";
    private static final String LOG_TAG = "GetFollowersTask";

    /**
     * Auth token for logged-in user.
     */
    private final AuthToken authToken;
    /**
     * The user whose followers are being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private final User targetUser;
    /**
     * Maximum number of followers to return (i.e., page size).
     */
    private final int limit;
    /**
     * The last follower returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    private final User lastFollower;

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastFollower = lastFollower;
    }

    @Override
    protected void runTask() {
        try {

            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            String lastFollowerAlias = lastFollower == null ? null : lastFollower.getAlias();

            FollowersRequest request = new FollowersRequest(authToken, targetUserAlias, limit, lastFollowerAlias);
            FollowersResponse response = getServerFacade().getFollowers(request, FollowService.URL_PATH_GET_FOLLOWERS);

            if (response.isSuccess()) {
                List<User> followers = response.getFollowers();
                boolean hasMorePages = response.getHasMorePages();

                Bundle extraBundle = new Bundle();
                extraBundle.putSerializable(PAGED_ITEM_KEY, (Serializable) followers);
                extraBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
                sendSuccessMessage(extraBundle);
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get followers", ex);
            sendExceptionMessage(ex);
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        super.loadSuccessBundle(msgBundle);
    }
}
