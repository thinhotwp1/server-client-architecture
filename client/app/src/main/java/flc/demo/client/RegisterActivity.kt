package flc.demo.client
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import flc.demo.client.network.ApiClient
import flc.demo.client.network.UserApi
import flc.demo.client.model.UserRegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailEditText = findViewById<EditText>(R.id.email)
        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val roleGroup = findViewById<RadioGroup>(R.id.role_group)
        val registerButton = findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            val selectedRoleId = roleGroup.checkedRadioButtonId
            val role = when (selectedRoleId) {
                R.id.role_admin -> 0 // Admin role
                else -> 1 // Default to user role
            }

            performRegistration(email, username, password, role)
        }
    }

    private fun performRegistration(email: String, username: String, password: String, role: Int) {
        val userApi = ApiClient.createService(UserApi::class.java)
        val registerCall = userApi.register(UserRegisterRequest(email, username, password, role))

        registerCall.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isSuccess = response.body() ?: false
                    if (isSuccess) {
                        Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()


                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration Failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Server Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                println(t)
                Toast.makeText(this@RegisterActivity, "Registration Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
