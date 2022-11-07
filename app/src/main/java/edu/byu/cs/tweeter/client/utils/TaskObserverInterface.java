package edu.byu.cs.tweeter.client.utils;

import android.os.Bundle;

public interface TaskObserverInterface {
    void onResponseReceived();
    void handleSuccess(Bundle bundle);
    void handleFailure(String message);
    void handleException(Exception exception);
}
