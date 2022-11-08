package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends BackgroundTask {

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";
    private static final String LOG_TAG = "LoginTask";

    /**
     * The user's first name.
     */
    private final String firstName;
    /**
     * The user's last name.
     */
    private final String lastName;
    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    private final String username;
    /**
     * The user's password.
     */
    private final String password;
    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private final String image;

    public RegisterTask(String firstName, String lastName, String username, String password, String image, Handler messageHandler) {
        super(messageHandler);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.image = image;
    }

    private FakeData getFakeData() {
        return FakeData.getInstance();
    }

    private Pair<User, AuthToken> doRegister() {
        User registeredUser = getFakeData().getFirstUser();
        AuthToken authToken = getFakeData().getAuthToken();
        return new Pair<>(registeredUser, authToken);
    }

    @Override
    protected void runTask() {

        try {
            RegisterRequest request = new RegisterRequest(username, password, firstName, lastName, image);
            LoginResponse response = getServerFacade().register(request, UserService.URL_PATH_REGISTER);

            if (response.isSuccess()) {
                Bundle msgBundle = new Bundle();
                msgBundle.putSerializable(USER_KEY, response.getUser());
                msgBundle.putSerializable(AUTH_TOKEN_KEY, response.getAuthToken());
                sendSuccessMessage(msgBundle);
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }

    }
}
