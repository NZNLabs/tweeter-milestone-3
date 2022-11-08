package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.service.StatusService;
import edu.byu.cs.tweeter.server.service.UserService;

/**
 * An AWS lambda function
 */
public class RegisterHandler implements RequestHandler<RegisterRequest, LoginResponse> {

    @Override
    public LoginResponse handleRequest(RegisterRequest request, Context context) {
        UserService service = new UserService();
        return service.register(request);
    }
}
