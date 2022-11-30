package edu.byu.cs.tweeter.server.factories;
import edu.byu.cs.tweeter.server.dao.*;

public class DaoFactoryImplementation implements DaoFactory {

    DatabaseFactoryImplementation dbFactory;

    public DaoFactoryImplementation(DatabaseFactoryImplementation dbFactory) {
        this.dbFactory = dbFactory;
    }

    UserDAO userDao = new UserDAO(dbFactory);
    StoryDAO storyDao = new StoryDAO(dbFactory);
    FollowerDAO followerDao = new FollowerDAO(dbFactory);
    FollowDAO followDao = new FollowDAO(dbFactory);
    FeedDAO feedDao = new FeedDAO(dbFactory);

    @Override
    public UserDAO getUserDAO() {
        return userDao;
    }

    @Override
    public StoryDAO getStoryDAO() {
        return storyDao;
    }

    @Override
    public FollowerDAO getFollowerDAO() {
        return followerDao;
    }

    @Override
    public FollowDAO getFollowDAO() {
        return followDao;
    }

    @Override
    public FeedDAO getFeedDAO() {
        return feedDao;
    }
}