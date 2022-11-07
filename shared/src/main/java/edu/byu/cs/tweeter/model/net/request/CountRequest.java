package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class CountRequest {

    private AuthToken authToken;
    private String userAlias;


    private CountRequest() {}

    public CountRequest(AuthToken authToken, String userAlias) {
        this.authToken = authToken;
        this.userAlias = userAlias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getUserAlias() {
        return userAlias;
    }
}
