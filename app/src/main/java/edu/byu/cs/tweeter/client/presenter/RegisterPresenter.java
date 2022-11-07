package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.utils.BaseViewInterface;
import edu.byu.cs.tweeter.client.utils.TaskObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends BasePresenter<RegisterPresenter.View> {

    public interface View extends BaseViewInterface {
        void navigateToUser(User user);
        void clearRegisterToast();
    }

    public RegisterPresenter(View view) {
        super(view);
    }

    public void register(Bitmap image, String firstName, String lastName, String alias, String password) {
        // Send register request.
        getUserService().register(getBase64EncodedBitmap(image), firstName, lastName, alias, password, registerObserver);
    }

    private String getBase64EncodedBitmap(Bitmap image) {
        // Convert image to byte array.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private UserService getUserService() {
        return new UserService();
    }

    private TaskObserverInterface registerObserver = new TaskObserverInterface() {

        @Override
        public void onResponseReceived() {}

        @Override
        public void handleSuccess(Bundle bundle) {
            User registeredUser = (User) bundle.getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) bundle.getSerializable(RegisterTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            view.clearRegisterToast();
            view.displayErrorMessage("Hello " + Cache.getInstance().getCurrUser().getName());
            view.navigateToUser(registeredUser);
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to register: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to register because of exception: " + exception.getMessage());
        }
    };


    public void validateRegistration(String firstName, String lastName, String alias, String password, Drawable imageDrawable) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageDrawable == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }
}
