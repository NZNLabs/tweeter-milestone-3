package edu.byu.cs.tweeter.server.factories;

import edu.byu.cs.tweeter.server.dao.IAuthDAO;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public interface DaoFactory {

    IUserDAO getUserDAO();

    IStoryDAO getStoryDAO();

    IFollowDAO getFollowDAO();

    IFeedDAO getFeedDAO();

    IAuthDAO getAuthDAO();

    IStatusDAO getStatusDAO();

}
