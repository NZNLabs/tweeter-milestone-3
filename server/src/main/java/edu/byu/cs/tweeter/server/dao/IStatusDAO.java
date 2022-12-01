package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.server.model.DBAuthToken;
import edu.byu.cs.tweeter.server.model.DBStatus;

public interface IStatusDAO {
    boolean postStatus(DBStatus request);
    StatusResponse getStory(StatusRequest request);
}
