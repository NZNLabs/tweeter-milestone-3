package edu.byu.cs.tweeter.server.factories;
import edu.byu.cs.tweeter.server.dao.*;

public class DaoFactoryImplementation implements DaoFactory {

    DatabaseFactoryImplementation dbFactory;

    public DaoFactoryImplementation(DatabaseFactoryImplementation dbFactory) {
        this.dbFactory = dbFactory;
    }

    UserDAO userDao = null;
    StoryDAO storyDao = null;
    FollowDAO followDao = null;
    FeedDAO feedDao = null;
    AuthDAO authDao = null;
    StatusDAO statusDao = null;

    @Override
    public IUserDAO getUserDAO() {
        if (userDao == null) { userDao = new UserDAO(dbFactory); }
        return userDao;
    }

    @Override
    public IStoryDAO getStoryDAO() {
        if (storyDao == null) { storyDao = new StoryDAO(dbFactory);}
        return storyDao;
    }

    @Override
    public IFollowDAO getFollowDAO() {
        if ( followDao == null) {followDao = new FollowDAO(dbFactory); }
        return followDao;
    }

    @Override
    public IFeedDAO getFeedDAO() {
        if ( feedDao == null) {feedDao = new FeedDAO(dbFactory); }
        return feedDao;
    }

    @Override
    public IAuthDAO getAuthDAO() {
        if ( authDao == null) {authDao = new AuthDAO(dbFactory); }
        return authDao;
    }

    @Override
    public IStatusDAO getStatusDAO() {
        if (statusDao == null) { statusDao = new StatusDAO(dbFactory); }
        return statusDao;
    }
}