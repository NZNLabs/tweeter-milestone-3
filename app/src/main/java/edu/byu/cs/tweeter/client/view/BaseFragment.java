package edu.byu.cs.tweeter.client.view;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

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
