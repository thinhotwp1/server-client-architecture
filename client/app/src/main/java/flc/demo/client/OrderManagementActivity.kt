package flc.demo.client

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import flc.demo.client.adapter.OrderManagementAdapter
import flc.demo.client.network.ApiClient
import flc.demo.client.network.Order
import flc.demo.client.network.OrderService
import flc.demo.client.network.UpdateOrderStatus
import kotlinx.coroutines.launch

class OrderManagementActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderManagementAdapter: OrderManagementAdapter
    private var orders = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_management)

        recyclerView = findViewById(R.id.recycler_view_orders_management) // Updated ID
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter
        orderManagementAdapter = OrderManagementAdapter(
            orders,
            onStatusChangeClick = { orderId -> showStatusDialog(orderId) },
            onViewDetailsClick = { orderId -> viewOrderDetails(orderId) }
        )
        recyclerView.adapter = orderManagementAdapter

        fetchOrders()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchOrders() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.createService(OrderService::class.java)
                    .getAllOrders()
                orders.clear()
                orders.addAll(response)
                orderManagementAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@OrderManagementActivity,
                    "Failed to load orders",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showStatusDialog(orderId: Long) {
        try {
            val statusOptions = arrayOf("Pending", "Shipping", "Completed")

            MaterialAlertDialogBuilder(this)
                .setTitle("Update Order Status")
                .setItems(statusOptions) { _, which ->
                    val selectedStatus = statusOptions[which]
                    updateOrderStatus(orderId, selectedStatus)
                }
                .show()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun viewOrderDetails(orderId: Long) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        intent.putExtra("orderId", orderId)
        startActivity(intent)
    }

    private fun updateOrderStatus(orderId: Long, status: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.createService(OrderService::class.java)
                    .updateOrderStatus(UpdateOrderStatus(orderId, status))

                if (response.isSuccessful && response.body() == true) {
                    Toast.makeText(
                        this@OrderManagementActivity,
                        "Order status updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchOrders() // Refresh order list after status update
                } else {
                    Toast.makeText(
                        this@OrderManagementActivity,
                        "Failed to update order status",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@OrderManagementActivity,
                    "Error updating order status",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

