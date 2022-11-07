package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends BackgroundTask {

    public static final String MORE_PAGES_KEY = "more-pages";
    private static final String LOG_TAG = "GetFeedTask";


    /**
     * Auth token for logged-in user.
     */
    private final AuthToken authToken;
    /**
     * The user whose feed is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private final User targetUser;
    /**
     * Maximum number of statuses to return (i.e., page size).
     */
    private final int limit;
    /**
     * The last status returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    private final Status lastStatus;
    /**
     * Message handler that will receive task results.
     */

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastStatus = lastStatus;
    }


    @Override
    protected void runTask() {

        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();

            StatusRequest request = new StatusRequest(authToken, targetUserAlias, limit, lastStatus);
            StatusResponse response = getServerFacade().getFeed(request, StatusService.URL_PATH_GET_FEED);

            if (response.isSuccess()) {
                List<Status> statuses = response.getStatuses();
                boolean hasMorePages = response.getHasMorePages();

                Bundle extraBundle = new Bundle();
                extraBundle.putSerializable(PAGED_ITEM_KEY, (Serializable) statuses);
                extraBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
                sendSuccessMessage(extraBundle);
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get feed", ex);
            sendExceptionMessage(ex);
        }

    }
}
