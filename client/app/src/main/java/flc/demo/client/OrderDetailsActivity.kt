package flc.demo.client

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.adapter.OrderDetailAdapter
import flc.demo.client.network.ApiClient
import flc.demo.client.network.OrderDetail
import flc.demo.client.network.OrderService
import kotlinx.coroutines.launch

class OrderDetailsActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderDetailAdapter: OrderDetailAdapter
    private var orderDetails = mutableListOf<OrderDetail>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        recyclerView = findViewById(R.id.recycler_view_order_details)
        recyclerView.layoutManager = LinearLayoutManager(this)

        orderDetailAdapter = OrderDetailAdapter(orderDetails)
        recyclerView.adapter = orderDetailAdapter

        val orderId = intent.getLongExtra("orderId", -1)
        fetchOrderDetails(orderId)
    }

    private fun fetchOrderDetails(orderId: Long) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.createService(OrderService::class.java).getOrderDetailsByOrderId(orderId)
                orderDetails.clear()
                orderDetails.addAll(response)
                orderDetailAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OrderDetailsActivity, "Failed to load order details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

