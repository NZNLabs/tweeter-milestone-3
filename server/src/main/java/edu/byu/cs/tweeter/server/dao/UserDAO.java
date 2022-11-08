package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * A DAO for accessing 'user' data from the database.
 */
public class UserDAO {

    public UserResponse getUser(UserRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getAuthToken() != null;
        assert request.getUserAlias() != null;

        return new UserResponse(getFakeData().findUserByAlias(request.getUserAlias()));
    }

    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
