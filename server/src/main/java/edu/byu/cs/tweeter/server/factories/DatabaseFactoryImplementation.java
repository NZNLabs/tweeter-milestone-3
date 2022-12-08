package edu.byu.cs.tweeter.server.factories;


import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.model.DBAuthToken;
import edu.byu.cs.tweeter.server.model.DBFeed;
import edu.byu.cs.tweeter.server.model.DBFollow;
import edu.byu.cs.tweeter.server.model.DBStatus;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class DatabaseFactoryImplementation implements DatabaseFactory {

    public static final DynamoDbEnhancedClient ddbEnhancedClient = DynamoDbEnhancedClient.create();

    private static DynamoDbTable<User> usersTable = null;
    private static DynamoDbTable<DBFollow> followTable = null;
    private static DynamoDbTable<DBAuthToken> authTable = null;
    private static DynamoDbTable<DBStatus> statusTable = null;
    private static DynamoDbTable<DBFeed> feedTable = null;

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

    @Override
    public DynamoDbTable<DBStatus> getStatusTable() {
        if (statusTable == null) {statusTable = ddbEnhancedClient.table("status", TableSchema.fromBean(DBStatus.class));}
        return statusTable;
    }

    @Override
    public DynamoDbTable<DBFeed> getFeedTable() {
        if (feedTable == null) {feedTable = ddbEnhancedClient.table("feed", TableSchema.fromBean(DBFeed.class));}
        return feedTable;
    }


}
