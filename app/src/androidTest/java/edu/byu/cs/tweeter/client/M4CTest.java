package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * This class exists purely to prove that tests in your androidTest/java folder have the correct dependencies.
 * Click on the green arrow to the left of the class declarations to run. These tests should pass if all
 * dependencies are correctly set up.
 */
public class M4CTest {

    private LoginPresenter.View loginViewMock;
    private LoginPresenter loginPresenterSpy;
    private ServerFacade serverFacade = new ServerFacade();

    private CountDownLatch countDownLatch;

    @BeforeEach
    public void setup() {
        // Called before each test, set up any common code between tests

        loginViewMock = Mockito.mock(LoginPresenter.View.class);
        LoginPresenter loginPresenter = new LoginPresenter(loginViewMock);
        loginPresenterSpy = Mockito.spy(loginPresenter);

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
    public void testButtonPress() throws InterruptedException {

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

        loginPresenterSpy.login("@jacob.zinn", "pass");
        awaitCountDownLatch();

        Mockito.verify(loginViewMock, Mockito.times(1)).navigateToUser(Mockito.any());

        Assertions.assertNotNull(Cache.getInstance().getCurrUser());
        Assertions.assertNotNull(Cache.getInstance().getCurrUserAuthToken());
        
    }
}