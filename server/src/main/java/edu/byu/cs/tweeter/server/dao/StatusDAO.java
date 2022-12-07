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
import edu.byu.cs.tweeter.server.model.DBStatus;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class StatusDAO extends AbstractDAO implements IStatusDAO {

    private final DynamoDbTable<DBStatus> ddbTable = this.dbFactory.getStatusTable();

    public StatusDAO(DatabaseFactory dbFactory) {super(dbFactory);}

    @Override
    public boolean postStatus(Status request) {
        try {
            DBStatus dbStatus = new DBStatus(request.user.getAlias(), request.datetime, DBStatus.getSerializedStatus(request));
            ddbTable.putItem(dbStatus);
            return true;
        } catch (Exception | AssertionError e) {
            System.out.println("Exception: postStatus");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public StatusResponse getStory(StatusRequest request) {

        try{
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(request.getUserAlias())
                    .build());

            Map<String, AttributeValue> lastEvaluatedKey = null;
            if (request.getLastStatus() != null) {
                lastEvaluatedKey = new java.util.HashMap<>(Collections.emptyMap());
                lastEvaluatedKey.put("username", AttributeValue.builder().s(request.getLastStatus().user.getAlias()).build());
                lastEvaluatedKey.put("dateTime", AttributeValue.builder().s(request.getLastStatus().datetime).build());
            }

            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .scanIndexForward(false)
                    .limit(request.getLimit())
                    .exclusiveStartKey(lastEvaluatedKey)
                    .build();

            // Get items in the table.
            Optional<Page<DBStatus>> result = ddbTable.query(queryRequest).stream().findFirst();

            // Display the results.
            if (result.isPresent()) {
                Page<DBStatus> page = result.get();
                List<DBStatus> dbStatuses = page.items();

                // deserialize status from db
                List<Status> statuses = new ArrayList<>();
                for (DBStatus dbStatus : dbStatuses) {
                    Status status = DBStatus.getDeserializedStatus(dbStatus.getStatus());
                    statuses.add(status);
                }
                System.out.println("FINISHING DESERIALIZATION: SIZE: " + statuses.size() );

                return new StatusResponse(statuses, page.lastEvaluatedKey() != null);
            } else {
                System.out.println("No results returned");
                return new StatusResponse(new ArrayList<>(), false);
            }
        } catch (Exception e) {
            String error = "Failed to get statuses" + e.getClass();
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new StatusResponse(error);
        }

    }
}
