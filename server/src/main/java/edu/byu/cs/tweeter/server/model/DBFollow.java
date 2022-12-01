package edu.byu.cs.tweeter.server.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;


@DynamoDbBean
public class DBFollow {

    public static final String SECONDARY_INDEX_FOLLOWEE = "followee_handle-follower_handle-index";

    public String follower_handle;
    public String follower_name;
    public String followee_handle;
    public String followee_name;

    public DBFollow(String follower_handle, String follower_name, String followee_handle, String followee_name) {
        this.follower_handle = follower_handle;
        this.follower_name = follower_name;
        this.followee_handle = followee_handle;
        this.followee_name = followee_name;
    }

    public DBFollow() {}

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = SECONDARY_INDEX_FOLLOWEE)
    public String getFollower_handle() {
        return follower_handle;
    }

    public void setFollower_handle(String follower_handle) {
        this.follower_handle = follower_handle;
    }

    public String getFollower_name() {
        return follower_name;
    }

    public void setFollower_name(String follower_name) {
        this.follower_name = follower_name;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = SECONDARY_INDEX_FOLLOWEE)
    @DynamoDbSortKey
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String followee_handle) {
        this.followee_handle = followee_handle;
    }

    public String getFollowee_name() {
        return followee_name;
    }

    public void setFollowee_name(String followee_name) {
        this.followee_name = followee_name;
    }
}
