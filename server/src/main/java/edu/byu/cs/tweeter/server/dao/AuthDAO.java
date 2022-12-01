package edu.byu.cs.tweeter.server.dao;

import java.util.List;
import java.util.Optional;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.factories.DatabaseFactory;
import edu.byu.cs.tweeter.server.model.DBAuthToken;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * A DAO for accessing 'user' data from the database.
 */
public class AuthDAO extends AbstractDAO implements IAuthDAO{

    private final DynamoDbTable<DBAuthToken> ddbTable = this.dbFactory.getAuthTable();

    public AuthDAO(DatabaseFactory dbFactory) {
        super(dbFactory);
    }

    @Override
    public boolean postToken(DBAuthToken request) {
        try {
            assert request.authToken != null;
            assert request.password != null;
            assert request.getDateTime() != null;
            assert request.getUsername() != null;

            System.out.println("updating auth");
            ddbTable.putItem(request);
            return true;
        } catch (AssertionError | Exception e) {
            e.printStackTrace();
            System.out.println("failed to update auth");
            return false;
        }
    }

    @Override
    public DBAuthToken getToken(String username) {
        try {
            assert username != null;

            Key key = Key.builder().partitionValue(username).build();
            return ddbTable.getItem(key);
        } catch (DynamoDbException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DBAuthToken getToken(AuthToken request) {
        try {
            assert request.getToken() != null;

            DynamoDbIndex<DBAuthToken> ddbTableIndex = ddbTable.index(DBAuthToken.SECONDARY_INDEX_AUTHTOKEN);
            AttributeValue attVal = AttributeValue.builder().s(request.getToken()).build();
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(attVal)
                    .build());

            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .build();

            // Get items in the table.
            SdkIterable<Page<DBAuthToken>> results = ddbTableIndex.query(queryRequest);

            // Display the results.
            DBAuthToken authToken = null;
            Optional<Page<DBAuthToken>> page = results.stream().findFirst();
            if (page.isPresent()) {
                List<DBAuthToken> tokens = page.get().items();
                if (tokens.size() == 1) {
                    authToken = tokens.get(0);
                }
            }

            return authToken;
        } catch (DynamoDbException e) {
            return null;
        }
    }
}
