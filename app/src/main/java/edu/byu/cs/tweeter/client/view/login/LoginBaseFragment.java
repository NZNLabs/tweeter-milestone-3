package edu.byu.cs.tweeter.client.view.login;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import edu.byu.cs.tweeter.client.view.main.MainInterface;

public class LoginBaseFragment extends Fragment {

    public Toast makeToastSafely(String message) {
        Context context = getContext();
        if (context == null) {
            Log.d("BASE_FRAGMENT", "context null, not showing toast");
            return null;
        }
        return Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
    }

    public void showToastSafely(String message) {
        Context context = getContext();
        if (context == null) {
            Log.d("BASE_FRAGMENT", "context null, not showing toast");
            return;
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

}
