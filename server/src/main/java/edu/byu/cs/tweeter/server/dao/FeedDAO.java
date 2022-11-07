package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * A DAO for accessing 'feed' data from the database.
 */
public class FeedDAO {

    public StatusResponse getFeed(StatusRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        List<Status> allStatuses = getDummyStatuses();
        List<Status> responseFollowers = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                int followersIndex = getStatusesStartingIndex(request.getLastStatus(), allStatuses);

                for(int limitCounter = 0; followersIndex < allStatuses.size() && limitCounter < request.getLimit(); followersIndex++, limitCounter++) {
                    responseFollowers.add(allStatuses.get(followersIndex));
                }

                hasMorePages = followersIndex < allStatuses.size();
            }
        }

        return new StatusResponse(responseFollowers, hasMorePages);
    }

    private int getStatusesStartingIndex(Status lastStatus, List<Status> allStatuses) {

        int statusesIndex = 0;

        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                Status curStatus = allStatuses.get(i);
                if(curStatus.getUser().getAlias().equals(lastStatus.getUser().getAlias()) && curStatus.getDate().equals(lastStatus.getDate())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusesIndex = i + 1;
                    break;
                }
            }
        }

        return statusesIndex;
    }

    List<Status> getDummyStatuses() {
        return getFakeData().getFakeStatuses();
    }

    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
