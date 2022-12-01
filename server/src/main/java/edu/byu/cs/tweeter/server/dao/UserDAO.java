package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.factories.DatabaseFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedResponse;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * A DAO for accessing 'user' data from the database.
 */
public class UserDAO extends AbstractDAO implements IUserDAO{

    private final DynamoDbTable<User> ddbTable = this.dbFactory.getUserTable();

    public UserDAO(DatabaseFactory dbFactory) {
        super(dbFactory);
    }

    @Override
    public UserResponse getUser(UserRequest request) {
        try {
            assert request.getAuthToken() != null;
            assert request.getUserAlias() != null;
            Key key = Key.builder().partitionValue(request.getUserAlias()).build();
            return new UserResponse(ddbTable.getItem(key));
        } catch (DynamoDbException | AssertionError e) {
            return new UserResponse(e.getMessage());
        }
    }

    @Override
    public boolean postUser(User request) {
        try {
            assert request.getAlias() != null;
            assert request.getFirstName() != null;
            assert request.getLastName() != null;
            assert request.getImageUrl() != null;

            ddbTable.putItem(request);
            return true;
        } catch (DynamoDbException | AssertionError e) {
            return false;
        }
    }

}
