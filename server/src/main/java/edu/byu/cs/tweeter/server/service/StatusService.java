package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.server.model.DBFeed;
import edu.byu.cs.tweeter.server.model.DBFollow;
import edu.byu.cs.tweeter.server.model.response.DBFollowResponse;
import edu.byu.cs.tweeter.server.util.AuthManagement;
import edu.byu.cs.tweeter.server.util.JsonSerializer;

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

            // getting followers
            System.out.println("getting followers: time: " + System.currentTimeMillis());
            List<DBFollow> followers = new ArrayList<>();
            boolean hasMorePages = true;
            while(hasMorePages) {
                String lastFollowerAlias = null;
                if (followers.size() > 0) {lastFollowerAlias = followers.get(followers.size() - 1).follower_handle;}

                FollowersRequest followersRequest = new FollowersRequest(request.getAuthToken(), request.getStatus().user.getAlias(), 100, lastFollowerAlias);
                DBFollowResponse response = getFollowDAO().getFollowers(followersRequest);
                hasMorePages = response.isHasMorePages();
                followers.addAll(response.getFollows());
            }
            System.out.println("followers list size: " + followers.size() + " & time: " + System.currentTimeMillis());

            // adding status to feed of each follower
            List<DBFeed> feedList = new ArrayList<>();
            for (DBFollow follower : followers) {
                feedList.add(new DBFeed(follower.follower_handle, status.getDate(), JsonSerializer.serialize(status)));
            }

            System.out.println("starting post feed batch: size: " + feedList.size());
            getFeedDAO().postFeedBatch(feedList);
            System.out.println("posted feed items successfully");

            boolean success = getStatusDAO().postStatus(status);
            if (success) {
                return new Response(true);
            } else {
                System.out.println("POST FAILURE");
                return new Response(false, "post failure");
            }



        } catch (Exception e) {
            System.out.println("POST FAILURE");
            String error = "Exception: postStatus " + e.getClass();
            e.printStackTrace();
            return new IsFollowerResponse(error);
        }
    }

}
