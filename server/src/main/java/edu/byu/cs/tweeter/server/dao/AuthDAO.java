package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.factories.DatabaseFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * A DAO for accessing 'user' data from the database.
 */
public class AuthDAO extends AbstractDAO implements IAuthDAO{

    private final DynamoDbTable<AuthToken> ddbTable = this.dbFactory.getAuthTable();

    public AuthDAO(DatabaseFactory dbFactory) {
        super(dbFactory);
    }

    @Override
    public AuthToken postToken(AuthToken request) {
        assert request.token != null;
        assert request.datetime != null;

        try {
            PutItemEnhancedRequest<AuthToken> putRequest = PutItemEnhancedRequest.builder(AuthToken.class).build();
            return ddbTable.putItemWithResponse(putRequest).attributes();
        } catch (DynamoDbException e) {
            return null;
        }
    }

    @Override
    public AuthToken getToken(AuthToken request) {
        assert request.getToken() != null;

        try {
            Key key = Key.builder().partitionValue(request.getToken()).build();
            return ddbTable.getItem(key);
        } catch (DynamoDbException e) {
            return null;
        }
    }
}
