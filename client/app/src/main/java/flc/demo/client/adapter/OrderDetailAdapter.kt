package flc.demo.client.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.R
import flc.demo.client.network.OrderDetail

class OrderDetailAdapter(
    private val orderDetails: List<OrderDetail>
) : RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {

    class OrderDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val price: TextView = itemView.findViewById(R.id.price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail, parent, false)
        return OrderDetailViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val orderDetail = orderDetails[position]
        holder.productName.text = "Product Name: ${orderDetail.productName}"
        holder.quantity.text = "Quantity: ${orderDetail.quantity}"
        holder.price.text = "Price: ${orderDetail.price}"
    }

    override fun getItemCount(): Int = orderDetails.size
}
