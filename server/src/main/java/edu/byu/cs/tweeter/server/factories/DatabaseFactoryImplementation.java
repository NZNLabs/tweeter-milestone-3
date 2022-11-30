package edu.byu.cs.tweeter.server.factories;


import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class DatabaseFactoryImplementation implements DatabaseFactory {

    private static final DynamoDbEnhancedClient ddbEnhancedClient = DynamoDbEnhancedClient.create();

    private static final DynamoDbTable<User> usersTable = ddbEnhancedClient.table("users", TableSchema.fromBean(User.class));
    private static final DynamoDbTable<Follow> followTable = ddbEnhancedClient.table("follow", TableSchema.fromBean(Follow.class));

    @Override
    public DynamoDbTable<User> getUserTable() {
        return usersTable;
    }

    @Override
    public DynamoDbTable<Follow> getFollowTable() {
        return followTable;
    }
}
