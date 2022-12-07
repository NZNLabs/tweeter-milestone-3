package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.server.model.DBFeed;

public interface IFeedDAO {
    boolean postFeed(DBFeed request);
    StatusResponse getFeed(StatusRequest request);
}
