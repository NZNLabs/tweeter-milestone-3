package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;

public interface IStoryDAO {
    Response postStatus(PostStatusRequest request);
    StatusResponse getStory(StatusRequest request);
}
