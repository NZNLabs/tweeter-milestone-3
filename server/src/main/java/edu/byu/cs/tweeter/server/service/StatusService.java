package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.server.util.AuthManagement;

/**
 * Contains the business logic
 */
public class StatusService extends AbstractService {

    public StatusResponse getFeed(StatusRequest request) {
        try {
            if(request.getUserAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a user alias");
            } else if(request.getLimit() <= 0) {
                throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
            }

            boolean isValid = AuthManagement.validateAuthToken(request.getAuthToken(), getAuthDAO());
            if (!isValid) { return new StatusResponse("expired"); }

            StatusResponse response = getFeedDAO().getFeed(request);
            if (!response.isSuccess()) {
                return new StatusResponse("Failed to get feed");
            }

            return new StatusResponse(response.getStatuses(), response.getHasMorePages());
        } catch (Exception e) {
            String error = "Exception: Failed to get followees " + e.getClass();
            System.out.println(error);
            return new StatusResponse(error);
        }
    }

    public StatusResponse getStory(StatusRequest request) {
        try {
            if(request.getUserAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a user alias");
            } else if(request.getLimit() <= 0) {
                throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
            }

            boolean isValid = AuthManagement.validateAuthToken(request.getAuthToken(), getAuthDAO());
            if (!isValid) { return new StatusResponse("expired"); }

            StatusResponse response = getStatusDAO().getStory(request);
            if (!response.isSuccess()) {
                return new StatusResponse("Failed to get story statuses");
            }

            return new StatusResponse(response.getStatuses(), response.getHasMorePages());
        } catch (Exception e) {
            String error = "Exception: getStory " + e.getClass();
            System.out.println(error);
            e.printStackTrace();
            return new StatusResponse(error);
        }
    }

    public Response postStatus(PostStatusRequest request) {

        try {
            if(request.getStatus() == null) {throw new RuntimeException("[Bad Request] Request needs to have a status");}
            Status status = request.getStatus();
            if (status.datetime == null) { throw new RuntimeException(); }
            if (status.post == null) { throw new RuntimeException(); }
            if (status.mentions == null) { throw new RuntimeException(); }
            if (status.urls == null) { throw new RuntimeException(); }
            if (status.user == null) { throw new RuntimeException(); }


            boolean isValid = AuthManagement.validateAuthToken(request.getAuthToken(), getAuthDAO());
            if (!isValid) { return new IsFollowerResponse("expired"); }

            boolean success = getStatusDAO().postStatus(status);
            if (success) {
                return new Response(true);
            } else {
                System.out.println("POST FAILURE");
                return new Response(false, "post failure");
            }
        } catch (Exception e) {
            String error = "Exception: postStatus " + e.getClass();
            e.printStackTrace();
            return new IsFollowerResponse(error);
        }
    }

}
