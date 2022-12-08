package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusQueueHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        StatusService service = new StatusService();

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            // msg body is serialized Status
            Response response = service.postStatusToFeeds(msg.getBody());
            System.out.println("NEW MESSAGE: " + msg);
            System.out.println("postStatusToFeeds success = " + response.isSuccess() + " & message = " + response.getMessage());
        }

        // triggered by sqs, so no need to return anything
        return null;
    }

}