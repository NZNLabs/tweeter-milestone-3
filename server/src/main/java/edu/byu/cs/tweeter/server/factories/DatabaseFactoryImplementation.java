package edu.byu.cs.tweeter.server.factories;


import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.model.DBAuthToken;
import edu.byu.cs.tweeter.server.model.DBFollow;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class DatabaseFactoryImplementation implements DatabaseFactory {

    private static final DynamoDbEnhancedClient ddbEnhancedClient = DynamoDbEnhancedClient.create();

    private static DynamoDbTable<User> usersTable = null;
    private static DynamoDbTable<DBFollow> followTable = null;
    private static DynamoDbTable<DBAuthToken> authTable = null;

    @Override
    public DynamoDbTable<User> getUserTable() {
        if (usersTable == null) {usersTable = ddbEnhancedClient.table("users", TableSchema.fromBean(User.class));}
        return usersTable;
    }

    @Override
    public DynamoDbTable<DBFollow> getFollowTable() {
        if (followTable == null) {followTable = ddbEnhancedClient.table("follow", TableSchema.fromBean(DBFollow.class));}
        return followTable;
    }

    @Override
    public DynamoDbTable<DBAuthToken> getAuthTable() {
        if (authTable == null) {authTable = ddbEnhancedClient.table("auth", TableSchema.fromBean(DBAuthToken.class));}
        return authTable;
    }

}
