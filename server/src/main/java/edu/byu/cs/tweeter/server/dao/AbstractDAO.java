package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.factories.DatabaseFactory;

public class AbstractDAO {

    DatabaseFactory dbFactory;

    public AbstractDAO(DatabaseFactory dbFactory) {
        this.dbFactory = dbFactory;
    }
}
