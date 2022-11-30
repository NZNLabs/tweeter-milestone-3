package edu.byu.cs.tweeter.server.factories;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

public interface DatabaseFactory {

    DynamoDbTable<User> getUserTable();
    DynamoDbTable<Follow> getFollowTable();
    DynamoDbTable<AuthToken> getAuthTable();

}
