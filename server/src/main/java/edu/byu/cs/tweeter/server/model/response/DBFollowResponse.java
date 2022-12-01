package edu.byu.cs.tweeter.server.model.response;

import java.util.List;

import edu.byu.cs.tweeter.server.model.DBFollow;

public class DBFollowResponse {

    private List<DBFollow> follows;
    private boolean hasMorePages;
    private boolean success;
    private String message;

    public DBFollowResponse(List<DBFollow> follows, boolean hasMorePages) {
        this.follows = follows;
        this.hasMorePages = hasMorePages;
        this.success = true;
    }

    public DBFollowResponse(String message) {
        this.success = false;
        this.message = message;
    }

    public List<DBFollow> getFollows() {
        return follows;
    }

    public void setFollows(List<DBFollow> follows) {
        this.follows = follows;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
