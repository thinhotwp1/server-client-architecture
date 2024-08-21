package flc.demo.client.model


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
