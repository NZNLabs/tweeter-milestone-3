package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

/**
 * An AWS lambda function that returns the number of users a user is following.
 */
public class GetFollowerCountHandler implements RequestHandler<CountRequest, CountResponse> {

    @Override
    public CountResponse handleRequest(CountRequest request, Context context) {
        FollowService service = new FollowService();
        return service.getFollowerCount(request);
    }

}
