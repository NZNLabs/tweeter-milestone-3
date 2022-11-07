package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {

    public StoryPresenter(PagedView<Status> view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {   // This guard is important for avoiding a race condition in the scrolling code.
            setLoading(true);
            view.setLoading(true);

            getStory(authToken, user, PAGE_SIZE, lastItem);
        }
    }

    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        getStatusService().getStory(authToken, targetUser, limit, lastStatus, getPagedObserver);
    }

    private StatusService getStatusService() {
        return new StatusService();
    }

}
