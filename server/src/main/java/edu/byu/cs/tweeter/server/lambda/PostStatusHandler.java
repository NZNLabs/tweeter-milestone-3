package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.service.StatusService;
import edu.byu.cs.tweeter.server.util.Constants;

/**
 * An AWS lambda function that returns the status for user.
 */
public class PostStatusHandler implements RequestHandler<PostStatusRequest, Response> {

    @Override
    public Response handleRequest(PostStatusRequest request, Context context) {
        StatusService service = new StatusService();
        return service.postStatus(request);
    }
}
