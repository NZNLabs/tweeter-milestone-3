package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.service.UserService;

/**
 * An AWS lambda function that logs a user in and returns the user object and an auth code for
 * a successful login.
 */
public class LogoutHandler implements RequestHandler<AuthToken, Response> {
    @Override
    public Response handleRequest(AuthToken authToken, Context context) {
        UserService userService = new UserService();
        return userService.logout(authToken);
    }
}
