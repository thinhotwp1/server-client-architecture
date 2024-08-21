package flc.demo.client.model

data class UserRegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    val role: Int
)
