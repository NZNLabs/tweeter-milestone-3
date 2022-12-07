package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.IAuthDAO;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.factories.DaoFactory;
import edu.byu.cs.tweeter.server.factories.DaoFactoryImplementation;
import edu.byu.cs.tweeter.server.factories.DatabaseFactoryImplementation;

public class AbstractService {
    public DaoFactory daoFactory = new DaoFactoryImplementation(new DatabaseFactoryImplementation());

    IFeedDAO getFeedDAO() {
        return daoFactory.getFeedDAO();
    }

    IStatusDAO getStatusDAO() {
        return daoFactory.getStatusDAO();
    }

    IAuthDAO getAuthDAO() {
        return daoFactory.getAuthDAO();
    }

    IFollowDAO getFollowDAO() {
        return daoFactory.getFollowDAO();
    }

    IUserDAO getUserDAO() {
        return daoFactory.getUserDAO();
    }
}
