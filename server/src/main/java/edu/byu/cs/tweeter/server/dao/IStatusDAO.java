package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;

public interface IStatusDAO {
    boolean postStatus(Status request);
    StatusResponse getStory(StatusRequest request);
}
