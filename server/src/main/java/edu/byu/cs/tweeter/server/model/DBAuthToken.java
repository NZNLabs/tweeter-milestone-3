package edu.byu.cs.tweeter.server.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
public class DBAuthToken {

    public static final String SECONDARY_INDEX_AUTHTOKEN = "authToken-index";

    // index to search by authtoken
    public String authToken;
    public String dateTime;
    public String password;

    public String username; // search by username and then check password by salt and hashed password

    public DBAuthToken() {}

    public DBAuthToken(String authToken, String dateTime, String password, String username) {
        this.authToken = authToken;
        this.dateTime = dateTime;
        this.password = password;
        this.username = username;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = SECONDARY_INDEX_AUTHTOKEN)
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
