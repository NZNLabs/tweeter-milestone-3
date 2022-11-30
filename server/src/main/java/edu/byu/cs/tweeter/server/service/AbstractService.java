package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.factories.DaoFactory;
import edu.byu.cs.tweeter.server.factories.DaoFactoryImplementation;
import edu.byu.cs.tweeter.server.factories.DatabaseFactoryImplementation;

public class AbstractService {
    public DaoFactory daoFactory = new DaoFactoryImplementation(new DatabaseFactoryImplementation());
}
