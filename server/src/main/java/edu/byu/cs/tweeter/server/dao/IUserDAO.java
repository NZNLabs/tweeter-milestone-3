package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

public interface IUserDAO {
    UserResponse getUser(String userAlias);
    boolean postUser(User request);
    void postUserBatch(List<User> users);
    void clearUsersDB();
}
