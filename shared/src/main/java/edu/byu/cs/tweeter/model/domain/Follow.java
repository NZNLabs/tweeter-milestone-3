package edu.byu.cs.tweeter.model.domain;


import java.io.Serializable;
import java.util.Objects;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Represents a follow relationship.
 */
@DynamoDbBean
public class Follow implements Serializable {

    public static final String SECONDARY_INDEX_FOLLOWEE = "followee-index";

    /**
     * The user doing the following.
     */
    public User follower;
    /**
     * The user being followed.
     */
    public User followee;

    public Follow() {}

    public Follow(User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
    }

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = SECONDARY_INDEX_FOLLOWEE)
    public User getFollower() {
        return follower;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = SECONDARY_INDEX_FOLLOWEE)
    @DynamoDbSortKey
    public User getFollowee() {
        return followee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow that = (Follow) o;
        return follower.equals(that.follower) &&
                followee.equals(that.followee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower, followee);
    }

    @Override
    public String toString() {
        return "Follow{" +
                "follower=" + follower.getAlias() +
                ", followee=" + followee.getAlias() +
                '}';
    }
}
