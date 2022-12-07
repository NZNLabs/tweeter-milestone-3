package edu.byu.cs.tweeter.client.view.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.presenter.RegisterPresenter;
import edu.byu.cs.tweeter.client.view.BaseFragment;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Implements the register screen.
 */
public class RegisterFragment extends LoginBaseFragment implements RegisterPresenter.View {
    private static final String LOG_TAG = "RegisterFragment";
    private static final int RESULT_IMAGE = 10;

    private EditText firstName;
    private EditText lastName;
    private EditText alias;
    private EditText password;
    private Button imageUploaderButton;
    private Button registerButton;
    private ImageView imageToUpload;
    private TextView errorView;
    private Toast registeringToast;

    private RegisterPresenter presenter;

    /**
     * Creates an instance of the fragment and places the user and auth token in an arguments
     * bundle assigned to the fragment.
     *
     * @return the fragment.
     */
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        presenter = new RegisterPresenter(this);

        firstName = view.findViewById(R.id.registerFirstName);
        lastName = view.findViewById(R.id.registerLastName);
        alias = view.findViewById(R.id.registerUsername);
        password = view.findViewById(R.id.registerPassword);
        imageUploaderButton = view.findViewById(R.id.imageButton);
        imageToUpload = view.findViewById(R.id.registerImage);
        registerButton = view.findViewById(R.id.registerButton);
        errorView = view.findViewById(R.id.registerError);

        imageUploaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RESULT_IMAGE);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Register and move to MainActivity.
                try {
                    presenter.validateRegistration(firstName.getText().toString(), lastName.getText().toString(), alias.getText().toString(), password.getText().toString(), imageToUpload.getDrawable());
                    errorView.setText(null);
                    registeringToast = makeToastSafely("Registering...");
                    if (registeringToast == null) {return;}
                    registeringToast.show();

                    Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
                    presenter.register(image, firstName.getText().toString(), lastName.getText().toString(), alias.getText().toString(), password.getText().toString());
                } catch (Exception e) {
                    errorView.setText(e.getMessage());
                }
            }
        });

        return view;
    }

    // Get image if uploaded from gallery.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
            imageUploaderButton.setText(R.string.afterUploadPicture);
        }
    }

    @Override
    public void navigateToUser(User user) {
        Context context = getContext();
        if (context == null) { return; }
        Intent intent = new Intent(context, MainActivity.class);
        Picasso.get().invalidate(user.getImageUrl());
        intent.putExtra(MainActivity.CURRENT_USER_KEY, user);
        startActivity(intent);
    }

    @Override
    public void displayErrorMessage(String message) {
        showToastSafely(message);
    }

    @Override
    public void logoutUser() {
        assert(false);
    }

    @Override
    public void clearRegisterToast() {
        if (registeringToast != null) {
            registeringToast.cancel();;
            registeringToast = null;
        }
    }
}
