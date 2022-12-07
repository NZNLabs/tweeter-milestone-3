package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.Response;
import edu.byu.cs.tweeter.server.factories.DatabaseFactory;
import edu.byu.cs.tweeter.server.model.DBFollow;
import edu.byu.cs.tweeter.server.model.response.DBFollowResponse;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO extends AbstractDAO implements IFollowDAO {

    private final DynamoDbTable<DBFollow> ddbTable = this.dbFactory.getFollowTable();

    public FollowDAO(DatabaseFactory dbFactory) {
        super(dbFactory);
    }

    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param followerAlias the User whose count of how many following is desired.
     * @return said count.
     */
    @Override
    public CountResponse getFollowingCount(String followerAlias) {
        AttributeValue attVal = AttributeValue.builder().s(followerAlias).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(attVal)
                .build());

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        // Get items in the table.
        SdkIterable<Page<DBFollow>> results = ddbTable.query(queryRequest);

        AtomicInteger count = new AtomicInteger();
        results.forEach(page -> {
            count.addAndGet(page.items().size());
        });

        return new CountResponse(count.intValue());
    }

    @Override
    public CountResponse getFollowerCount(String userAlias) {
        System.out.println("getFollowerCount start");

        DynamoDbIndex<DBFollow> ddbTableIndex = ddbTable.index(DBFollow.SECONDARY_INDEX_FOLLOWEE);
        AttributeValue attVal = AttributeValue.builder()
                .s(userAlias)
                .build();

        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(attVal)
                .build());

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        // Get items in the table.
        SdkIterable<Page<DBFollow>> results = ddbTableIndex.query(queryRequest);

        AtomicInteger count = new AtomicInteger();
        results.forEach(page -> {
            count.addAndGet(page.items().size());
        });

        System.out.println("FOLLOWER COUNT");
        System.out.println(count.intValue());
        return new CountResponse(count.intValue());
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    @Override
    public DBFollowResponse getFollowees(FollowingRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        try{
            System.out.println("getFollowees 1 follower alias : " + request.getFollowerAlias());
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(request.getFollowerAlias())
                    .build());

            System.out.println("getFollowees 2");

            Map<String, AttributeValue> lastEvaluatedKey = null;
            if (request.getLastFolloweeAlias() != null) {
                lastEvaluatedKey = new java.util.HashMap<>(Collections.emptyMap());
                lastEvaluatedKey.put("followee_handle", AttributeValue.builder().s(request.getLastFolloweeAlias()).build());
                lastEvaluatedKey.put("follower_handle", AttributeValue.builder().s(request.getFollowerAlias()).build());
            }

            System.out.println("getFollowees 3");
            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .scanIndexForward(true)
                    .limit(request.getLimit())
                    .exclusiveStartKey(lastEvaluatedKey)
                    .build();


            System.out.println("getFollowees 4");
            // Get items in the table.
            Optional<Page<DBFollow>> result = ddbTable.query(queryRequest).stream().findFirst();

            System.out.println("getFollowees 5");
            // Display the results.
            if (result.isPresent()) {
                Page<DBFollow> page = result.get();
                List<DBFollow> followees = page.items();
                System.out.println("getFollowees size: " + followees.size());
                return new DBFollowResponse(followees, page.lastEvaluatedKey() != null);
            } else {
                System.out.println("No results returned");
                return new DBFollowResponse(new ArrayList<>(), false);
            }
        } catch (Exception e) {
            String error = "Failed to get followees 2" + e.getClass();
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new DBFollowResponse(error);
        }
    }

    @Override
    public DBFollowResponse getFollowers(FollowersRequest request) {
        try{
            DynamoDbIndex<DBFollow> ddbTableIndex = ddbTable.index(DBFollow.SECONDARY_INDEX_FOLLOWEE);
            AttributeValue attVal = AttributeValue.builder()
                    .s(request.getFolloweeAlias())
                    .build();

            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(attVal)
                    .build());

            Map<String, AttributeValue> lastEvaluatedKey = null;
            if (request.getLastFollowerAlias() != null) {
                lastEvaluatedKey = new java.util.HashMap<>(Collections.emptyMap());
                lastEvaluatedKey.put("followee_handle", AttributeValue.builder().s(request.getFolloweeAlias()).build());
                lastEvaluatedKey.put("follower_handle", AttributeValue.builder().s(request.getLastFollowerAlias()).build());
            }

            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .scanIndexForward(true)
                    .limit(10)
                    .exclusiveStartKey(lastEvaluatedKey)
                    .build();

            // Get items in the table.
            Optional<Page<DBFollow>> result = ddbTableIndex.query(queryRequest).stream().findFirst();

            // Display the results.
            if (result.isPresent()) {
                Page<DBFollow> page = result.get();
                List<DBFollow> followers = page.items();
                System.out.println("getFollowers size: " + followers.size());
                return new DBFollowResponse(followers, page.lastEvaluatedKey() != null);
            } else {
                System.out.println("getFollowers No results returned");
                return new DBFollowResponse(new ArrayList<>(), false);
            }
        } catch (Exception e) {
            System.out.println(e.getClass());
            e.printStackTrace();
            return new DBFollowResponse("getFollowers: Failed to get followers " + e.getClass());
        }
    }

    @Override
    public IsFollowerResponse getIsFollower(IsFollowerRequest request) {
        try {
            assert request.getFolloweeAlias() != null;
            assert request.getFollowerAlias() != null;

            Key key = Key.builder().partitionValue(request.getFollowerAlias()).sortValue(request.getFolloweeAlias()).build();
            DBFollow follow = ddbTable.getItem(key);
            return new IsFollowerResponse(follow != null);
        } catch (Exception | AssertionError e) {
            String error =  "Failed check follow status " + e.getClass();
            System.out.println(error);
            return new IsFollowerResponse(error);
        }
    }

    @Override
    public Response postFollow(DBFollow request) {
        try {
            assert request.getFollowee_handle() != null;
            assert request.getFollower_handle() != null;
            assert request.getFollowee_name() != null;
            assert request.getFollower_name() != null;

            ddbTable.putItem(request);
            return new Response(true);
        } catch (Exception | AssertionError e) {
            System.out.println("Failed to post follow: " + e.getClass());
            return new Response(false, "Failed to follow " + e.getClass());
        }
    }

    @Override
    public Response deleteFollow(DBFollow request) {
        try {
            assert request.getFollowee_handle() != null;
            assert request.getFollower_handle() != null;
            assert request.getFollowee_name() != null;
            assert request.getFollower_name() != null;

            ddbTable.deleteItem(request);
            return new Response(true);
        } catch (Exception | AssertionError e) {
            System.out.println("Failed to delete follow: " + e.getClass());
            return new Response(false, "Failed to follow " + e.getClass());
        }
    }

}
