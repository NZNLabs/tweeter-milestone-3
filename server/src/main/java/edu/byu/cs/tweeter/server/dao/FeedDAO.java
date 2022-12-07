package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.server.factories.DatabaseFactory;
import edu.byu.cs.tweeter.server.model.DBFeed;
import edu.byu.cs.tweeter.server.model.DBStatus;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * A DAO for accessing 'feed' data from the database.
 */
public class FeedDAO extends AbstractDAO implements IFeedDAO {

    private final DynamoDbTable<DBFeed> ddbTable = this.dbFactory.getFeedTable();

    public FeedDAO(DatabaseFactory dbFactory) {
        super(dbFactory);
    }

    @Override
    public boolean postFeed(DBFeed request) {
        try {
            ddbTable.putItem(request);
            return true;
        } catch (Exception | AssertionError e) {
            System.out.println("Exception: postFeed");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public StatusResponse getFeed(StatusRequest request) {

        try{
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(request.getUserAlias())
                    .build());

            System.out.println("getFollowees 2");

            Map<String, AttributeValue> lastEvaluatedKey = null;
            if (request.getLastStatus() != null) {
                lastEvaluatedKey = new java.util.HashMap<>(Collections.emptyMap());
                lastEvaluatedKey.put("ownerAlias", AttributeValue.builder().s(request.getLastStatus().user.getAlias()).build());
                lastEvaluatedKey.put("dateTime", AttributeValue.builder().s(request.getLastStatus().datetime).build());
            }

            System.out.println("getFollowees 3");
            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .scanIndexForward(false)
                    .limit(request.getLimit())
                    .exclusiveStartKey(lastEvaluatedKey)
                    .build();


            System.out.println("getFollowees 4");
            // Get items in the table.
            Optional<Page<DBFeed>> result = ddbTable.query(queryRequest).stream().findFirst();

            System.out.println("getFollowees 5");
            // Display the results.
            if (result.isPresent()) {
                Page<DBFeed> page = result.get();
                List<DBFeed> dbFeedItems = page.items();

                System.out.println("STARTING DESERIALIZATION: SIZE: " + dbFeedItems.size() );
                // deserialize status from db
                List<Status> statuses = new ArrayList<>();
                for (DBFeed feedItem : dbFeedItems) {
                    Status status = DBStatus.getDeserializedStatus(feedItem.getStatus());
                    statuses.add(status);
                }
                System.out.println("FINISHING DESERIALIZATION: SIZE: " + statuses.size() );

                return new StatusResponse(statuses, page.lastEvaluatedKey() != null);
            } else {
                System.out.println("No results returned");
                return new StatusResponse(new ArrayList<>(), false);
            }
        } catch (Exception e) {
            String error = "Failed to get feed items" + e.getClass();
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new StatusResponse(error);
        }

    }
}
