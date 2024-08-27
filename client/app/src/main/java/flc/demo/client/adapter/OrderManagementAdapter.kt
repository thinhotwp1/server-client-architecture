package flc.demo.client.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.R
import flc.demo.client.network.Order

class OrderManagementAdapter(
    private val orders: List<Order>,
    private val onStatusChangeClick: (Long) -> Unit,
    private val onViewDetailsClick: (Long) -> Unit
) : RecyclerView.Adapter<OrderManagementAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_management_order, parent, false)
        return OrderViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderId.text = "Order ID: ${order.orderId}"
        holder.orderStatus.text = "Status: ${order.status}"
        holder.orderDate.text = "Date: ${order.orderDate}"
        holder.orderTotal.text = "Total: $${order.total}"

        // Handle status change button click
        holder.buttonChangeStatus.setOnClickListener {
            onStatusChangeClick(order.orderId)
        }

        // Handle item click for viewing details
        holder.itemView.setOnClickListener {
            onViewDetailsClick(order.orderId)
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.orderId)
        val orderStatus: TextView = view.findViewById(R.id.orderStatus)
        val orderDate: TextView = view.findViewById(R.id.orderDate)
        val orderTotal: TextView = view.findViewById(R.id.orderTotal)
        val buttonChangeStatus: Button = view.findViewById(R.id.buttonChangeStatus)
    }
}

