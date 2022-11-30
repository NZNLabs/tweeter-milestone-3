package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.factories.DatabaseFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
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
        assert request.getAuthToken() != null;
        assert request.getUserAlias() != null;

        try {
            Key key = Key.builder().partitionValue(request.getUserAlias()).build();
            return new UserResponse(ddbTable.getItem(key));
        } catch (DynamoDbException e) {
            return new UserResponse(e.getMessage());
        }
    }

}
