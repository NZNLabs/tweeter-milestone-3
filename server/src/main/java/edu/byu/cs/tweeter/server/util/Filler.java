package edu.byu.cs.tweeter.server.util;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.factories.DaoFactory;

public class Filler {

    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 10000;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@jacob.zinn";

    public static void fillDatabase(DaoFactory daoFactory) {

        // Get instance of DAOs by way of the Abstract Factory Pattern
        IUserDAO userDAO = daoFactory.getUserDAO();
        IFollowDAO followDAO = daoFactory.getFollowDAO();

        List<String> followers = new ArrayList<>();
        List<User> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String firstName = "Guy ";
            String lastName = String.valueOf(i);
            String alias = "guy" + i;

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            User user = new User();
            user.setAlias(alias);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(alias);
        }

//        // Call the DAOs for the database logic
//        if (users.size() > 0) {
//            userDAO.postUserBatch(users);
//        }
        if (followers.size() > 0) {
            followDAO.postFollowBatch(followers, FOLLOW_TARGET);
        }
    }
}