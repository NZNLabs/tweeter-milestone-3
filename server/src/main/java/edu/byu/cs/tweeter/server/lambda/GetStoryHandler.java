package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.server.service.StatusService;

/**
 * An AWS lambda function that returns the status for user.
 */
public class GetStoryHandler implements RequestHandler<StatusRequest, StatusResponse> {

    @Override
    public StatusResponse handleRequest(StatusRequest request, Context context) {
        StatusService service = new StatusService();
        return service.getStory(request);
    }
}
