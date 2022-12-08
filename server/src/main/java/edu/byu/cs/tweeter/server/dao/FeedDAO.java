package edu.byu.cs.tweeter.server.dao;

import static edu.byu.cs.tweeter.server.factories.DatabaseFactoryImplementation.ddbEnhancedClient;

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
import edu.byu.cs.tweeter.server.model.DBFollow;
import edu.byu.cs.tweeter.server.model.DBStatus;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * A DAO for accessing 'feed' data from the database.
 */
public class FeedDAO extends AbstractDAO implements IFeedDAO {

    private final DynamoDbTable<DBFeed> ddbTable = this.dbFactory.getFeedTable();

    public FeedDAO(DatabaseFactory dbFactory) {
        super(dbFactory, ddbEnhancedClient);
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

    @Override
    public void postFeedBatch(List<DBFeed> feedList) {
        try {
            System.out.println("START postFeedBatch() feed size: " + feedList.size());
            List<DBFeed> batchToWrite = new ArrayList<>();
            for (DBFeed item : feedList) {
                batchToWrite.add(item);

                if (batchToWrite.size() == 25) {
                    // package this batch up and send to DynamoDB.
                    writeChunkOfFeedDTOs(batchToWrite);
                    batchToWrite = new ArrayList<>();
                }
            }

            // write any remaining
            if (batchToWrite.size() > 0) {
                // package this batch up and send to DynamoDB.
                writeChunkOfFeedDTOs(batchToWrite);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeChunkOfFeedDTOs(List<DBFeed> feedList) {
        if(feedList.size() > 25) {
            throw new RuntimeException("Too many follows to write: size: " + feedList.size());
        }

        WriteBatch.Builder<DBFeed> writeBuilder = WriteBatch.builder(DBFeed.class).mappedTableResource(ddbTable);
        for (DBFeed item : feedList) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(ddbTable).size() > 0) {
                writeChunkOfFeedDTOs(result.unprocessedPutItemsForTable(ddbTable));
            }

        } catch (DynamoDbException e) {
            System.out.println("FAILED JZ HERE");
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void clearFeedDB() {
        try {
            System.out.println("STARING CLEAN OF FEED");
            PageIterable<DBFeed> scan = ddbTable.scan();
            Object[] items = scan.items().stream().toArray();
            for (Object item : items) {
                DBFeed feed = ((DBFeed)item);
                ddbTable.deleteItem(feed);
            }
            System.out.println("FINISH CLEAN OF FEED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
