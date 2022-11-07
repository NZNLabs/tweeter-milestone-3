package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * Contains all the information needed to make a request to have the server return the next page of
 * followers for a specified follower.
 */
public class StatusRequest {

    private AuthToken authToken;
    private String userAlias;
    private int limit;
    private Status lastStatus;


    private StatusRequest() {}

    public StatusRequest(AuthToken authToken, String userAlias, int limit, Status lastStatus) {
        this.authToken = authToken;
        this.userAlias = userAlias;
        this.limit = limit;
        this.lastStatus = lastStatus;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public Status getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
    }
}
