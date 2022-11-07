package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.service.FollowService;

/**
 * An AWS lambda function
 */
public class GetUnfollowHandler implements RequestHandler<UnfollowRequest, Response> {

    @Override
    public Response handleRequest(UnfollowRequest request, Context context) {
        FollowService service = new FollowService();
        return service.getUnfollow(request);
    }
}
