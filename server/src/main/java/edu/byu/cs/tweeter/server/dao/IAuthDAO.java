package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.server.model.DBAuthToken;

public interface IAuthDAO {
    boolean postToken(DBAuthToken request);
    DBAuthToken getToken(AuthToken request);
    DBAuthToken getToken(String username);
}
