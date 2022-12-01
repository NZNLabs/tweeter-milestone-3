package edu.byu.cs.tweeter.server.util;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.IAuthDAO;
import edu.byu.cs.tweeter.server.model.DBAuthToken;

public class AuthManagement {

    private static final long TOKEN_EXPIRATION_LENGTH = 1000*60*60; // 60 minutes

    public static boolean isAuthTokenExpired(DBAuthToken authToken) {
        System.out.println("isAuthTokenExpired current - tokenTime: " + (System.currentTimeMillis() - Long.parseLong(authToken.getDateTime())));
        return (System.currentTimeMillis() - Long.parseLong(authToken.getDateTime())) > TOKEN_EXPIRATION_LENGTH;
    }

    public static boolean updateAuthToken(DBAuthToken dbAuthToken, IAuthDAO authDAO) {
        try {
            dbAuthToken.setDateTime(String.valueOf(System.currentTimeMillis()));
            authDAO.postToken(dbAuthToken);
            return true;
        } catch (Exception e) {
            System.out.println("Exception: updateAuthToken: e: " + e.getMessage());
            return  false;
        }
    }

    public static boolean validateAuthToken(AuthToken authToken, IAuthDAO authDAO) {
        // check for auth expiration
        try {
            DBAuthToken dbAuthToken = authDAO.getToken(authToken);
            System.out.println("AuthToken expired. AuthTime: " + authToken.datetime + " DBAuthTime: " + dbAuthToken.dateTime);
            if (AuthManagement.isAuthTokenExpired(dbAuthToken)) {
                System.out.println("its expired");
                return false;
            }

            return updateAuthToken(dbAuthToken, authDAO);
        } catch (Exception e) {
            System.out.println("Exception: validateAuthToken: e: " + e.getClass());
            e.printStackTrace();
            return false;
        }
    }
}
