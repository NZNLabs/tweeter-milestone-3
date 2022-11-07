package edu.byu.cs.tweeter.client.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;


/**
 * Handles messages from the background task indicating that the task is done, by invoking
 * methods on the observer.
 */
public class GenericMessageHandler<Observer extends TaskObserverInterface> extends Handler {

    private final Observer observer;

    public GenericMessageHandler(Observer observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle bundle = message.getData();
        boolean success = bundle.getBoolean(GetFollowingTask.SUCCESS_KEY);
        observer.onResponseReceived();
        if (success) {
            observer.handleSuccess(bundle);
        } else if (bundle.containsKey(GetFollowingTask.MESSAGE_KEY)) {
            String errorMessage = bundle.getString(GetFollowingTask.MESSAGE_KEY);
            observer.handleFailure(errorMessage);
        } else if (bundle.containsKey(GetFollowingTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(GetFollowingTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }
}
