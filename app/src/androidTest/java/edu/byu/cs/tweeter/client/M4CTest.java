package edu.byu.cs.tweeter.client;

import android.os.Bundle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.utils.TaskObserverInterface;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * This class exists purely to prove that tests in your androidTest/java folder have the correct dependencies.
 * Click on the green arrow to the left of the class declarations to run. These tests should pass if all
 * dependencies are correctly set up.
 */
public class M4CTest {

    private LoginPresenter.View loginViewMock;
    private LoginPresenter loginPresenterSpy;

    private MainPresenter.View mainViewMock;
    private MainPresenter mainPresenterSpy;

    private StatusService statusServiceSpy;
    private GetStoryObserver observer;


    private CountDownLatch countDownLatch;





    @BeforeEach
    public void setup() {
        // Called before each test, set up any common code between tests

        loginViewMock = Mockito.mock(LoginPresenter.View.class);
        LoginPresenter loginPresenter = new LoginPresenter(loginViewMock);
        loginPresenterSpy = Mockito.spy(loginPresenter);

        mainViewMock = Mockito.mock(MainPresenter.View.class);

        statusServiceSpy = Mockito.spy(new StatusService());
        observer = new GetStoryObserver();

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Test
    public void testAsserts() {
        Assertions.assertTrue(true);
    }

    @Test
    public void postStatusFlowTest() throws InterruptedException {

        String statusUserAlias = "guy1";
        String statusUserPassword = "pass";
        String statusMessage = "mock test V3";


        // LOGGING IN
        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(loginViewMock).navigateToUser(Mockito.any());

        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(loginViewMock).displayErrorMessage(Mockito.anyString());

        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(loginViewMock).logoutUser();

        loginPresenterSpy.login(statusUserAlias, statusUserPassword);
        awaitCountDownLatch();

        Mockito.verify(loginViewMock, Mockito.times(1)).navigateToUser(Mockito.any());

        Assertions.assertNotNull(Cache.getInstance().getCurrUser());
        Assertions.assertNotNull(Cache.getInstance().getCurrUserAuthToken());


        // POSTING STATUS
        MainPresenter mainPresenter = new MainPresenter(mainViewMock, Cache.getInstance().getCurrUserAuthToken());
        mainPresenterSpy = Mockito.spy(mainPresenter);

        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(mainViewMock).displaySuccessMessage(Mockito.any());

        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(mainViewMock).displayErrorMessage(Mockito.anyString());

        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(mainViewMock).logoutUser();

        resetCountDownLatch();
        mainPresenterSpy.postStatus(statusMessage);
        awaitCountDownLatch();

        Mockito.verify(mainViewMock, Mockito.times(1)).displaySuccessMessage("Successfully Posted!");


        // GET USER'S STORY
        resetCountDownLatch();
        GetStoryTask storyTask = statusServiceSpy.getStoryTask(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(), 25, null, observer);
        BackgroundTaskUtils.runTask(storyTask);
        awaitCountDownLatch();

        // data exists
        Assertions.assertNotNull(observer.statuses);
        Assertions.assertTrue(observer.statuses.size() > 0);

        // first item in story must be the one recently posted
        Assertions.assertEquals(observer.statuses.get(0).post, statusMessage);
        Assertions.assertEquals(observer.statuses.get(0).user.getAlias(), Cache.getInstance().getCurrUser().getAlias());

        // no errors returned
        Assertions.assertTrue(observer.isSuccess);
        Assertions.assertNull(observer.message);
        Assertions.assertNull(observer.exception);

    }

    public class GetStoryObserver extends TaskObserverInterface {
        public boolean isSuccess;
        Exception exception;
        String message;
        List<Status> statuses;


        @Override
        public void onResponseReceived() {
            countDownLatch.countDown();
        }

        @Override
        public void handleSuccess(Bundle bundle) {
            statuses = (List<Status>) bundle.getSerializable(BackgroundTask.PAGED_ITEM_KEY);
            isSuccess = true;
            message = null;
            exception = null;
        }

        @Override
        public void handleFailure(String message) {
            String errorMessage = "Failed to retrieve paged item: " + message;
            statuses = null;
            isSuccess = false;
            this.message = errorMessage;
            exception = null;
        }

        @Override
        public void handleException(Exception exception) {
            String errorMessage = "Failed to retrieve paged item because of exception: " + exception.getMessage();
            statuses = null;
            isSuccess = false;
            this.message = errorMessage;
            this.exception = exception;
        }

        @Override
        public void logout() {
            isSuccess = false;
        }
    };
}