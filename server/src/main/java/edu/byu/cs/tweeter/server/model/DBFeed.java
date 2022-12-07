package edu.byu.cs.tweeter.server.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;


@DynamoDbBean
public class DBFeed {

    public static final String SECONDARY_INDEX_AUTHOR_ALIAS = "followee_handle-follower_handle-index";

    public String ownerAlias;
    // public String authorAlias;
    public String dateTime;
    public String status;

    public DBFeed() {}

    public DBFeed(String ownerAlias, String authorAlias, String dateTime, String status) {
        this.ownerAlias = ownerAlias;
        // this.authorAlias = authorAlias;
        this.dateTime = dateTime;
        this.status = status;
    }

    @DynamoDbPartitionKey
    public String getOwnerAlias() {
        return ownerAlias;
    }

    public void setOwnerAlias(String ownerAlias) {
        this.ownerAlias = ownerAlias;
    }

//    @DynamoDbSecondaryPartitionKey(indexNames = SECONDARY_INDEX_AUTHOR_ALIAS)
//    public String getAuthorAlias() {
//        return authorAlias;
//    }
//
//    public void setAuthorAlias(String authorAlias) {
//        this.authorAlias = authorAlias;
//    }

    @DynamoDbSortKey
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}