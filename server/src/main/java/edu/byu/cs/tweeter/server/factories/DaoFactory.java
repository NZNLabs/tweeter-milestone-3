package edu.byu.cs.tweeter.server.factories;

import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IFollowerDAO;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public interface DaoFactory {

    IUserDAO getUserDAO();

    IStoryDAO getStoryDAO();

    IFollowerDAO getFollowerDAO();

    IFollowDAO getFollowDAO();

    IFeedDAO getFeedDAO();

}
