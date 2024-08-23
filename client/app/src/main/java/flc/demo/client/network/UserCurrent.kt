package flc.demo.client.network

import flc.demo.client.model.User


object UserCurrent {

    private val currentUser = ThreadLocal<User>()

    fun setCurrentUser(user: User) {
        currentUser.set(user)
    }

    fun getCurrentUser(): User? {
        return currentUser.get()
    }

    fun clearCurrentUser() {
        currentUser.remove()
    }
}
