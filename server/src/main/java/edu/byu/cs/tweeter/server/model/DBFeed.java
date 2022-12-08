package edu.byu.cs.tweeter.server.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;


@DynamoDbBean
public class DBFeed {

    public String ownerAlias;
    public String dateTime;
    public String status;

    public DBFeed() {}

    public DBFeed(String ownerAlias, String dateTime, String status) {
        this.ownerAlias = ownerAlias;
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