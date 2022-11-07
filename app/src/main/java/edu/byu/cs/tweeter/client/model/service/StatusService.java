package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.utils.GenericMessageHandler;
import edu.byu.cs.tweeter.client.utils.TaskObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class StatusService {

    public static final String URL_PATH_GET_FEED = "/getFeed";
    public static final String URL_PATH_GET_STORY = "/getStory";
    public static final String URL_PATH_POST_STATUS = "/postStatus";

    public StatusService() {}

    // FEED
    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus, TaskObserverInterface observer) {
        GetFeedTask feedTask = getFeedTask(authToken, targetUser, limit, lastStatus, observer);
        BackgroundTaskUtils.runTask(feedTask);
    }

    // This method is public so it can be accessed by test cases
    public GetFeedTask getFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus, TaskObserverInterface observer) {
        return new GetFeedTask(authToken, targetUser, limit, lastStatus, new GenericMessageHandler<>(observer));
    }

    // STORY
    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus, TaskObserverInterface observer) {
        GetStoryTask storyTask = getStoryTask(authToken, targetUser, limit, lastStatus, observer);
        BackgroundTaskUtils.runTask(storyTask);
    }

    // This method is public so it can be accessed by test cases
    public GetStoryTask getStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus, TaskObserverInterface observer) {
        return new GetStoryTask(authToken, targetUser, limit, lastStatus, new GenericMessageHandler<>(observer));
    }

    // POST STATUS
    public void postStatus(AuthToken authToken, Status status, TaskObserverInterface observer) {
        PostStatusTask task = getPostStatusTask(authToken, status, observer);
        BackgroundTaskUtils.runTask(task);
    }

    // This method is public so it can be accessed by test cases
    public PostStatusTask getPostStatusTask(AuthToken authToken, Status status, TaskObserverInterface observer) {
        return new PostStatusTask(authToken, status, new GenericMessageHandler<>(observer));
    }

}
