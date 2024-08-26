package flc.demo.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val manageCategoriesButton = findViewById<Button>(R.id.shopping_button)
        val manageProductsButton = findViewById<Button>(R.id.go_to_cart_button)
        val ordersHistoryButton = findViewById<Button>(R.id.history_order_button)

        manageCategoriesButton.setOnClickListener {
            val intent = Intent(this, ProductSelectionActivity::class.java)
            startActivity(intent)
        }

        manageProductsButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        ordersHistoryButton.setOnClickListener {
            val intent = Intent(this, OrdersHistory::class.java)
            startActivity(intent)
        }
    }
}
