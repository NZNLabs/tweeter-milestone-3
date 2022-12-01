package edu.byu.cs.tweeter.server.model;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.util.JsonSerializer;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class DBStatus {

    public String username;
    public String dateTime;
    public String status;

    public DBStatus() {}

    private static String getSerializedStatus(Status status) {
        return JsonSerializer.serialize(status);
    }

    private static Status getDeserializedStatus(String status) {
        return JsonSerializer.deserialize(status, Status.class);
    }


    public DBStatus(String username, String dateTime, String status) {
        this.username = username;
        this.dateTime = dateTime;
        this.status = status;
    }

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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
