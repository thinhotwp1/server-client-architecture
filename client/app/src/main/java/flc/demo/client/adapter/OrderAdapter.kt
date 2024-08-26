package flc.demo.client.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.R
import flc.demo.client.network.Order
import java.util.Locale

class OrderAdapter(
    private val orders: List<Order>,
    private val onItemClick: (Long) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.orderId)
        val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        val total: TextView = itemView.findViewById(R.id.orderTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderId.text = "Order: ${order.username.toUpperCase(Locale.ROOT) + " " +order.orderDate.substring(0, 10)}"
        holder.orderStatus.text = "Status: ${order.status}"
        holder.orderDate.text = "Date: ${order.orderDate.substring(0, 10)}"
        holder.total.text = "Total: ${order.total}"
        holder.itemView.setOnClickListener {
            onItemClick(order.orderId)
        }
    }

    override fun getItemCount(): Int = orders.size
}
