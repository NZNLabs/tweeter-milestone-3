package edu.byu.cs.tweeter.client.utils;

import android.os.Bundle;

public abstract class TaskObserverInterface {
    public abstract void onResponseReceived();
    public abstract void handleSuccess(Bundle bundle);
    public void handleFailure(String message) {
        if (message != null && message.contains("expired")) {logout();}
    }
    public abstract void handleException(Exception exception);
    public abstract void logout();
}
