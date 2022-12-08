package edu.byu.cs.tweeter.client;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.FollowingPresenter;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.client.utils.TaskObserverInterface;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;

/**
 * This class exists purely to prove that tests in your androidTest/java folder have the correct dependencies.
 * Click on the green arrow to the left of the class declarations to run. These tests should pass if all
 * dependencies are correctly set up.
 */
public class AndroidTestsWorkingTest {
    class Foo {
        public void foo() {

        }
    }

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
    public void testMockitoSpy() {
        Foo f = Mockito.spy(new Foo());
        f.foo();
        Mockito.verify(f).foo();
    }
    @Test
    public void testMockitoMock() {
        Foo f = Mockito.mock(Foo.class);
        f.foo();
        Mockito.verify(f).foo();
    }

//    @Rule
//    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class);


    @Test
    public void testButtonPress() {

        loginPresenterSpy.login("@jacob.zinn", "pass");
//        Mockito.verify(loginPresenterSpy.getLoginObserver(), Mockito.times(1)).handleSuccess(Mockito.any());
//        Mockito.when(loginPresenterSpy.getLoginObserver().handleSuccess(Mockito.any()));
        Mockito.verify(loginViewMock, Mockito.times(1)).navigateToUser(Mockito.any());
//        Mockito.verify(loginObserverMock, Mockito.times(1)).handleSuccess(Mockito.any());


//
//        onViewWithId(R.id.login).perform(click());
//
//        onViewWithId()
//        Foo f = Mockito.mock().toString();


    }
}