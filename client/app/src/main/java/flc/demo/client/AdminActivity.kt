package flc.demo.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val manageCategoriesButton = findViewById<Button>(R.id.manage_categories_button)
        val manageProductsButton = findViewById<Button>(R.id.manage_products_button)
        val manageOrders = findViewById<Button>(R.id.manage_orders_button)

        manageCategoriesButton.setOnClickListener {
            val intent = Intent(this, CategoryManagementActivity::class.java)
            startActivity(intent)
        }

        manageProductsButton.setOnClickListener {
            val intent = Intent(this, ProductManagementActivity::class.java)
            startActivity(intent)
        }

        manageOrders.setOnClickListener {
            val intent = Intent(this, OrderManagementActivity::class.java)
            startActivity(intent)
        }
    }
}
