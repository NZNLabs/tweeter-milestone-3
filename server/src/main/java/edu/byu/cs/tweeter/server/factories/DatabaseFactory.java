package edu.byu.cs.tweeter.server.factories;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.model.DBAuthToken;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

public interface DatabaseFactory {

    DynamoDbTable<User> getUserTable();
    DynamoDbTable<Follow> getFollowTable();
    DynamoDbTable<DBAuthToken> getAuthTable();

}
