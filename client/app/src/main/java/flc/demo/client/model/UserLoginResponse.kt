package flc.demo.client.model

data class UserLoginResponse(
    val username: String,
    val email: String,
    val role: Int
)