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
    public UserResponse getUser(String userAlias) {
        try {
            assert userAlias != null;
            Key key = Key.builder().partitionValue(userAlias).build();
            User user = ddbTable.getItem(key);
            if (user == null) return new UserResponse("User not found " + userAlias);
            return new UserResponse(user);
        } catch (Exception | AssertionError e) {
            String error = "Failed to get user " + e.getClass();
            System.out.println(error);
            return new UserResponse(error);
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
