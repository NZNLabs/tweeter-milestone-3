package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The presenter for the "following" functionality of the application.
 */
public class FeedPresenter extends PagedPresenter<Status> {

    public FeedPresenter(PagedView<Status> view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    /**
     * Called by the view to request that another page of "following" users be loaded.
     */
    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            view.setLoading(true);
            setLoading(true);

            getFeed(authToken, user, PAGE_SIZE, lastItem);
        }
    }

    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        getFeedService().getFeed(authToken, targetUser, limit, lastStatus, getPagedObserver);
    }

    public StatusService getFeedService() {
        return new StatusService();
    }
}
