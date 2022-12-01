package edu.byu.cs.tweeter.client.presenter

import android.os.Bundle
import edu.byu.cs.tweeter.client.cache.Cache
import edu.byu.cs.tweeter.client.model.service.UserService
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask
import edu.byu.cs.tweeter.client.utils.BaseViewInterface
import edu.byu.cs.tweeter.client.utils.TaskObserverInterface
import edu.byu.cs.tweeter.model.domain.AuthToken
import edu.byu.cs.tweeter.model.domain.User

class LoginPresenter(view: View): BasePresenter<LoginPresenter.View>(view) {

    // methods that the presenter can call on the view
    interface View: BaseViewInterface {
        fun clearErrorMessage()

        fun displayInfoMessage(message: String)
        fun clearInfoMessage()

        fun setLoading(value: Boolean)
        fun navigateToUser(user: User)
    }

    // methods that the view can call on the presenter
    fun login(username: String, password: String) {
        val validationError = validateLogin(username, password)
        if (validationError == null) {
            view.clearErrorMessage()
            view.displayInfoMessage("Logging In...")
            UserService().login(username, password, loginObserver)
        } else {
            view.displayErrorMessage(validationError)
        }
    }

    fun validateLogin(alias: String, password: String): String? {
        if (alias.first() != '@') { return "Alias must begin with @." }
        if (alias.length < 2) { return "Alias must contain 1 or more characters after the @." }
        if (password.isEmpty()) { return "Password cannot be empty." }
        return null
    }

    private val loginObserver: TaskObserverInterface = object : TaskObserverInterface {
        override fun onResponseReceived() {}
        override fun handleSuccess(bundle: Bundle) {
            val loggedInUser = bundle.getSerializable(LoginTask.USER_KEY) as User
            val authToken = bundle.getSerializable(LoginTask.AUTH_TOKEN_KEY) as AuthToken

            // Cache user session information
            Cache.getInstance().currUser = loggedInUser
            Cache.getInstance().currUserAuthToken = authToken

            view.clearInfoMessage()
            view.clearErrorMessage()

            view.displayInfoMessage("Hello ${Cache.getInstance().currUser.name}")
            view.navigateToUser(loggedInUser)
        }

        override fun handleFailure(message: String) {
            view.displayInfoMessage("Failed to login: $message")
        }

        override fun handleException(exception: java.lang.Exception) {
            view.displayInfoMessage("Failed to login because of exception: " + exception.message)
        }
    }
}