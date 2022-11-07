package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends BackgroundTask {
    public static final String MORE_PAGES_KEY = "more-pages";

    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;
    /**
     * The user whose story is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private User targetUser;
    /**
     * Maximum number of statuses to return (i.e., page size).
     */
    private int limit;
    /**
     * The last status returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    private Status lastStatus;

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastStatus = lastStatus;
    }

    private FakeData getFakeData() {
        return FakeData.getInstance();
    }

    private Pair<List<Status>, Boolean> getStory() {
        Pair<List<Status>, Boolean> pageOfStatus = getFakeData().getPageOfStatus(lastStatus, limit);
        return pageOfStatus;
    }

    @Override
    protected void runTask() {
        Pair<List<Status>, Boolean> pageOfStatus = getStory();

        List<Status> statuses = pageOfStatus.getFirst();
        boolean hasMorePages = pageOfStatus.getSecond();

        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, true);
        msgBundle.putSerializable(PAGED_ITEM_KEY, (Serializable) statuses);
        msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);

        sendSuccessMessage(msgBundle);
    }
}