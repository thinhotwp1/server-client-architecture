package flc.demo.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import flc.demo.client.ui.theme.ClientTheme

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val manageCategoriesButton = findViewById<Button>(R.id.manage_categories_button)
        val manageProductsButton = findViewById<Button>(R.id.manage_products_button)

        manageCategoriesButton.setOnClickListener {
            val intent = Intent(this, CategoryManagementActivity::class.java)
            startActivity(intent)
        }

        manageProductsButton.setOnClickListener {
            val intent = Intent(this, ProductManagementActivity::class.java)
            startActivity(intent)
        }
    }
}
