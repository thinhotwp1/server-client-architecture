package flc.demo.client.network

import flc.demo.client.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("/users/register")
    fun register(@Body user: UserRegisterRequest): Call<Boolean>

    @POST("/users/login")
    fun login(@Body user: UserLoginRequest): Call<UserLoginResponse>
}
