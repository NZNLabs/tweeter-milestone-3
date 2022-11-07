package edu.byu.cs.tweeter.client.model.service

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask
import edu.byu.cs.tweeter.client.utils.GenericMessageHandler
import edu.byu.cs.tweeter.client.utils.TaskObserverInterface
import edu.byu.cs.tweeter.model.domain.AuthToken
import java.util.concurrent.Executors

class UserService {

    companion object {
        const val URL_PATH_LOGIN = "/login"
    }

    fun login(username: String, password: String, observer: TaskObserverInterface) {
        val loginTask = LoginTask(username, password, GenericMessageHandler(observer))
        val executor = Executors.newSingleThreadExecutor()
        executor.execute(loginTask)
    }

    fun logout(authToken: AuthToken, observer: TaskObserverInterface) {
        val task = LogoutTask(authToken, GenericMessageHandler(observer))
        val executor = Executors.newSingleThreadExecutor()
        executor.execute(task)
    }

    fun register(image: String, firstName: String, lastName: String, alias: String, password: String, observer: TaskObserverInterface ) {
        val task = RegisterTask(firstName, lastName, alias, password, image, GenericMessageHandler(observer))
        val executor = Executors.newSingleThreadExecutor()
        executor.execute(task)
    }

    fun getUser(authToken: AuthToken, alias: String, observer: TaskObserverInterface) {
        val task = GetUserTask(authToken, alias, GenericMessageHandler(observer))
        val executor = Executors.newSingleThreadExecutor()
        executor.execute(task)
    }
}