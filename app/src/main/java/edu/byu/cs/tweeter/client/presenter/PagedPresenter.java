package edu.byu.cs.tweeter.client.presenter;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.utils.BaseViewInterface;
import edu.byu.cs.tweeter.client.utils.TaskObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends BasePresenter<PagedPresenter.PagedView<T>> {
    private static final String LOG_TAG = "PagedPresenter";
    public static final int PAGE_SIZE = 10;

    public final User user;
    public final AuthToken authToken;

    public boolean hasMorePages = true;
    public boolean isLoading = false;
    public T lastItem;

    public PagedPresenter(PagedView<T> view, User user, AuthToken authToken) {
        super(view);
        this.user = user;
        this.authToken = authToken;
    }

    public interface PagedView<T> extends BaseViewInterface {
        void navigateToUser(User user);
        void setLoading(boolean value);
        void addItems(List<T> items);
    }

    public void getUser(String userAlias) {
        getUserService().getUser(authToken, userAlias, getUserObserver);
    }

    private UserService getUserService() {
        return new UserService();
    }

    private final TaskObserverInterface getUserObserver = new TaskObserverInterface() {

        @Override
        public void onResponseReceived() {}

        @Override
        public void handleSuccess(Bundle bundle) {
            User user = (User) bundle.getSerializable(GetUserTask.USER_KEY);
            view.navigateToUser(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to get user's profile:" + message);
            super.handleFailure(message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to get user's profile because of exception: " + exception.getMessage());
        }
        @Override
        public void logout() {
            view.logoutUser();
        }
    };

    public final TaskObserverInterface getPagedObserver = new TaskObserverInterface() {

        @Override
        public void onResponseReceived() {
            view.setLoading(false);
            setLoading(false);
        }

        @Override
        public void handleSuccess(Bundle bundle) {
            List<T> statuses = (List<T>) bundle.getSerializable(BackgroundTask.PAGED_ITEM_KEY);
            boolean hasMorePages = bundle.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
            setLastItem((statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null);
            setHasMorePages(hasMorePages);

            view.addItems(statuses);
        }

        @Override
        public void handleFailure(String message) {
            String errorMessage = "Failed to retrieve paged item: " + message;
            Log.e(LOG_TAG, errorMessage);

            view.displayErrorMessage(errorMessage);
            super.handleFailure(message);
        }

        @Override
        public void handleException(Exception exception) {
            String errorMessage = "Failed to retrieve paged item because of exception: " + exception.getMessage();
            Log.e(LOG_TAG, errorMessage, exception);

            view.displayErrorMessage(errorMessage);
        }

        @Override
        public void logout() {
            view.logoutUser();
        }
    };

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public T getLastItem() {
        return lastItem;
    }

    public void setLastItem(T lastItem) {
        this.lastItem = lastItem;
    }

}
