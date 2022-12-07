package edu.byu.cs.tweeter.client.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.view.BaseFragment;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Implements the login screen.
 */
public class LoginFragment extends LoginBaseFragment implements LoginPresenter.View {
    private static final String LOG_TAG = "LoginFragment";

    private LoginPresenter presenter;

    private Toast loginInToast;
    private EditText alias;
    private EditText password;
    private TextView errorView;

    /**
     * Creates an instance of the fragment and places the user and auth token in an arguments
     * bundle assigned to the fragment.
     *
     * @return the fragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        alias = view.findViewById(R.id.loginUsername);
        password = view.findViewById(R.id.loginPassword);
        errorView = view.findViewById(R.id.loginError);
        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener((View.OnClickListener) view1 -> {
            // Login and move to MainActivity.
            presenter.login(alias.getText().toString(), password.getText().toString());
        });

        presenter = new LoginPresenter(this);
        return view;
    }

    // Methods executed by presenter
    @Override
    public void displayErrorMessage(@NonNull String message) {
        errorView.setText(message);
    }

    @Override
    public void logoutUser() {
        assert(false);
    }

    @Override
    public void setLoading(boolean value) {

    }

    @Override
    public void clearErrorMessage() {
        errorView.setText("");
    }

    @Override
    public void displayInfoMessage(@NonNull String message) {
        clearInfoMessage();
        loginInToast = makeToastSafely(message);
        if (loginInToast == null) {return;}
        loginInToast.show();
    }

    @Override
    public void clearInfoMessage() {
        if (loginInToast != null) {
            loginInToast.cancel();;
            loginInToast = null;
        }
    }

    @Override
    public void navigateToUser(@NonNull User user) {
        Context context = getContext();
        if (context == null) { return; }
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.CURRENT_USER_KEY, user);
        startActivity(intent);
    }
}
