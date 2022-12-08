package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.factories.DatabaseFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public class AbstractDAO {

    DatabaseFactory dbFactory;
    DynamoDbEnhancedClient enhancedClient;

    public AbstractDAO(DatabaseFactory dbFactory, DynamoDbEnhancedClient enhancedClient) {
        this.dbFactory = dbFactory;
        this.enhancedClient = enhancedClient;
    }
}
