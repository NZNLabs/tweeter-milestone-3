package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;

/**
 * Contains the business logic
 */
public class StatusService {

    public StatusResponse getFeed(StatusRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFeedDAO().getFeed(request);
    }

    public StatusResponse getStory(StatusRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getStoryDAO().getStory(request);
    }

    public Response postStatus(PostStatusRequest request) {
        if(request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }
        return getStoryDAO().postStatus(request);
    }

    FeedDAO getFeedDAO() {
        return new FeedDAO();
    }

    StoryDAO getStoryDAO() {return new StoryDAO();}



}
