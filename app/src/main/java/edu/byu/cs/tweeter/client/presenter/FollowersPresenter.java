package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The presenter for the "follower" functionality of the application.
 */
public class FollowersPresenter extends PagedPresenter<User> {


    public FollowersPresenter(PagedView<User> view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    /**
     * Called by the view to request that another page of "follower" users be loaded.
     */
    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);

            getFollowers(authToken, user, PAGE_SIZE, lastItem);
        }
    }

    /**
     * Requests the users that the user specified in the request is followed by. Uses information in
     * the request object to limit the number of followers returned and to return the next set of
     * followers after any that were returned for a previous request. This is an asynchronous
     * operation.
     *
     * @param authToken    the session auth token.
     * @param targetUser   the user for whom followers are being retrieved.
     * @param limit        the maximum number of followers to return.
     * @param lastFollower the last follower returned in the previous request (can be null).
     */
    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower) {
        getFollowService().getFollowers(authToken, targetUser, limit, lastFollower, getPagedObserver);
    }

    /**
     * Returns an instance of {@link FollowService}. Allows mocking of the FollowService class
     * for testing purposes. All usages of FollowService should get their FollowService
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    private FollowService getFollowService() {
        return new FollowService();
    }

}
