package edu.byu.cs.tweeter.server.dao;

import static edu.byu.cs.tweeter.server.factories.DatabaseFactoryImplementation.ddbEnhancedClient;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.factories.DatabaseFactory;
import edu.byu.cs.tweeter.server.model.DBFollow;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * A DAO for accessing 'user' data from the database.
 */
public class UserDAO extends AbstractDAO implements IUserDAO{

    private final DynamoDbTable<User> ddbTable = this.dbFactory.getUserTable();

    public UserDAO(DatabaseFactory dbFactory) {
        super(dbFactory, ddbEnhancedClient);
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

    @Override
    public void postUserBatch(List<User> users) {
        try {
            List<User> batchToWrite = new ArrayList<>();
            for (User u : users) {
                batchToWrite.add(u);

                if (batchToWrite.size() == 25) {
                    // package this batch up and send to DynamoDB.
                    writeChunkOfUserDTOs(batchToWrite);
                    batchToWrite = new ArrayList<>();
                }
            }

            // write any remaining
            if (batchToWrite.size() > 0) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserDTOs(batchToWrite);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeChunkOfUserDTOs(List<User> users) {
        if(users.size() > 25)
            throw new RuntimeException("Too many users to write");

        WriteBatch.Builder<User> writeBuilder = WriteBatch.builder(User.class).mappedTableResource(ddbTable);
        for (User item : users) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(ddbTable).size() > 0) {
                writeChunkOfUserDTOs(result.unprocessedPutItemsForTable(ddbTable));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void clearUsersDB() {
        try {
            System.out.println("STARING CLEAN OF USER");
            PageIterable<User> scan = ddbTable.scan();
            Object[] items = scan.items().stream().toArray();
            for (Object item : items) {
                User user = ((User)item);
                ddbTable.deleteItem(user);
            }
            System.out.println("FINISH CLEAN OF USER");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
