package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;

public interface IFeedDAO {
    StatusResponse getFeed(StatusRequest request);
}
