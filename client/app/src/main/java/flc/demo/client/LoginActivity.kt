package flc.demo.client
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import flc.demo.client.model.User
import flc.demo.client.network.UserCurrent
import flc.demo.client.network.ApiClient
import flc.demo.client.network.UserApi
import flc.demo.client.model.UserLoginRequest
import flc.demo.client.model.UserLoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerLink = findViewById<TextView>(R.id.register_link)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            performLogin(username, password)
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(username: String, password: String) {
        try {
            val userApi = ApiClient.createService(UserApi::class.java)
            val loginCall = userApi.login(UserLoginRequest(username, password))

            loginCall.enqueue(object : Callback<UserLoginResponse> {
                override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {
                    if (response.isSuccessful) {
                        val userLoginResponse = response.body()
                        if (userLoginResponse != null) {
                            // Save user data into UserCurrent
                            val user: User = User(userLoginResponse.username, userLoginResponse.role)
                            UserCurrent.setCurrentUser(user)

                            Toast.makeText(this@LoginActivity, "Welcome : ${userLoginResponse.username}", Toast.LENGTH_SHORT).show()
                            if (user.role==0){
                                val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                                startActivity(intent)
                                finish()
                            }else{
                                val intent = Intent(this@LoginActivity, UserActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Unexpected Error: Empty Response", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                    t.printStackTrace() // Log the full stack trace for debugging
                    Toast.makeText(this@LoginActivity, "Login Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        } catch (e: Exception) {
            println("Login Error: $e")
            Toast.makeText(this@LoginActivity, "Unexpected Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


}
