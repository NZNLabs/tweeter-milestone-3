package edu.byu.cs.tweeter.client.presenter;

import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.utils.BaseViewInterface;
import edu.byu.cs.tweeter.client.utils.TaskObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends BasePresenter<MainPresenter.View> {

    private static final String LOG_TAG = "MainPresenter";

    private final AuthToken authToken;

    public interface View extends BaseViewInterface {
        void updateFollowerCount(int count);
        void updateFollowingCount(int count);
        void updateFollowButton(boolean shouldUpdate);
        void toggleIsFollowerButton(boolean isFollower);
        void displaySuccessMessage(String message);
        void clearPostToast();
        void clearLogoutToast();
        void enableFollowButton(boolean isEnabled);
        void updateSelectedUserFollowingAndFollowers();
        void logoutUser();
    }

    public MainPresenter(View view, AuthToken authToken) {
        super(view);
        this.authToken = authToken;
    }

    public void isFollower(User follower, User followee) {
        getFollowService().isFollow(authToken, follower, followee, isFollowObserver);
    }

    public void unfollow(User user) {
        getFollowService().postUnfollow(authToken, user, postUnfollowObserver);
    }

    public void follow(User user) {
        getFollowService().postFollow(authToken, user, postFollowObserver);
    }

    public void getFollowerCount(User user) {
        getFollowService().getFollowerCountObserver(authToken, user, getFollowerCountObserver);
    }

    public void getFollowingCount(User user) {
        getFollowService().getFollowingCountObserver(authToken, user, getFollowingCountObserver);
    }

    public void postStatus(String post) {
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            getStatusService().postStatus(authToken, newStatus, postStatusObserver);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayErrorMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public void logout() {
        getUserService().logout(authToken, logoutObserver);
    }

    private UserService getUserService() {
        return new UserService();
    }
    private FollowService getFollowService() {
        return new FollowService();
    }
    private StatusService getStatusService() {return new StatusService();}

    private final TaskObserverInterface logoutObserver = new TaskObserverInterface() {
        @Override
        public void onResponseReceived() {}

        @Override
        public void handleSuccess(Bundle bundle) {
            view.clearLogoutToast();
            view.logoutUser();
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to logout: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to logout because of exception: " + exception.getMessage());
        }
    };

    private final TaskObserverInterface getFollowerCountObserver = new TaskObserverInterface() {
        @Override
        public void onResponseReceived() {}
        @Override
        public void handleSuccess(Bundle bundle) {
            int count = bundle.getInt(GetFollowersCountTask.COUNT_KEY);
            view.updateFollowerCount(count);
        }
        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to get follower count: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to get follower count because of exception: " + exception.getMessage());
        }
    };

    private final TaskObserverInterface getFollowingCountObserver = new TaskObserverInterface() {
        @Override
        public void onResponseReceived() {}
        @Override
        public void handleSuccess(Bundle bundle) {
            int count = bundle.getInt(GetFollowingCountTask.COUNT_KEY);
            view.updateFollowingCount(count);
        }
        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to get following count: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to get following count because of exception: " + exception.getMessage());
        }
    };

    private final TaskObserverInterface isFollowObserver = new TaskObserverInterface() {
        @Override
        public void onResponseReceived() {}
        @Override
        public void handleSuccess(Bundle bundle) {
            boolean isFollower = bundle.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            view.toggleIsFollowerButton(isFollower);
        }
        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to determine following relationship: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to determine following relationship because of exception: " + exception.getMessage());
        }
    };

    private final TaskObserverInterface postFollowObserver = new TaskObserverInterface() {
        @Override
        public void onResponseReceived() {
            view.enableFollowButton(true);
        }
        @Override
        public void handleSuccess(Bundle bundle) {
            view.updateSelectedUserFollowingAndFollowers();
            view.updateFollowButton(false);
        }
        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to follow: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to follow because of exception: " + exception.getMessage());
        }
    };

    private final TaskObserverInterface postUnfollowObserver = new TaskObserverInterface() {
        @Override
        public void onResponseReceived() {
            view.enableFollowButton(true);
        }
        @Override
        public void handleSuccess(Bundle bundle) {
            view.updateSelectedUserFollowingAndFollowers();
            view.updateFollowButton(true);
        }
        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to unfollow: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to unfollow because of exception: " + exception.getMessage());
        }
    };

    private final TaskObserverInterface postStatusObserver = new TaskObserverInterface() {
        @Override
        public void onResponseReceived() {}
        @Override
        public void handleSuccess(Bundle bundle) {
            view.clearPostToast();
            view.displaySuccessMessage("Successfully Posted!");
        }
        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to post status: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to post status because of exception: " + exception.getMessage());
        }
    };

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);
                word = word.substring(0, index);
                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    private int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    private List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

}
