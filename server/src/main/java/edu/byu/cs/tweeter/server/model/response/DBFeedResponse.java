package edu.byu.cs.tweeter.server.model.response;

import java.util.List;

import edu.byu.cs.tweeter.server.model.DBFeed;
import edu.byu.cs.tweeter.server.model.DBFollow;

public class DBFeedResponse {

    private List<DBFeed> feedItems;
    private boolean hasMorePages;
    private boolean success;
    private String message;

    public DBFeedResponse(List<DBFeed> feedItems, boolean hasMorePages) {
        this.feedItems = feedItems;
        this.hasMorePages = hasMorePages;
        this.success = true;
    }

    public DBFeedResponse(String message) {
        this.success = false;
        this.message = message;
    }

}
