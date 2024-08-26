package flc.demo.client

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.adapter.OrderAdapter
import flc.demo.client.network.ApiClient
import flc.demo.client.network.Order
import flc.demo.client.network.OrderService
import flc.demo.client.network.UserCurrent
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class OrdersHistory : ComponentActivity() {

    private lateinit var orderService: OrderService
    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private var orders = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_history)

        orderService = ApiClient.createService(OrderService::class.java)
        recyclerView = findViewById(R.id.recycler_view_orders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        orderAdapter = OrderAdapter(orders) { orderId ->
            viewOrderDetails(orderId)
        }
        recyclerView.adapter = orderAdapter

        fetchOrders()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchOrders() {
        UserCurrent.getCurrentUser()?.let {
            lifecycleScope.launch {
                try {
                    val response = orderService.getOrdersByUsername(it.username)
                    orders.clear()
                    orders.addAll(response)
                    orderAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@OrdersHistory, "Failed to load orders", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun viewOrderDetails(orderId: Long) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        intent.putExtra("orderId", orderId)
        startActivity(intent)
    }
}
