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
    public IUserDAO getUserDAO() {
        return userDao;
    }

    @Override
    public IStoryDAO getStoryDAO() {
        return storyDao;
    }

    @Override
    public IFollowerDAO getFollowerDAO() {
        return followerDao;
    }

    @Override
    public IFollowDAO getFollowDAO() {
        return followDao;
    }

    @Override
    public IFeedDAO getFeedDAO() {
        return feedDao;
    }
}