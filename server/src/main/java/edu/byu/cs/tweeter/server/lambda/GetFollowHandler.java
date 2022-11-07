package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.service.FollowService;

/**
 * An AWS lambda function
 */
public class GetFollowHandler implements RequestHandler<FollowRequest, Response> {

    @Override
    public Response handleRequest(FollowRequest request, Context context) {
        FollowService service = new FollowService();
        return service.getFollow(request);
    }
}
