package flc.demo.client.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.R
import flc.demo.client.network.Category

import flc.demo.client.network.CartItem

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onDeleteClick: (Long) -> Unit // Pass product ID to delete
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.product_name)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantity_text_view)
        val priceTextView: TextView = itemView.findViewById(R.id.product_price)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(cartItem: CartItem) {
            productNameTextView.text = cartItem.product.name
            quantityTextView.text = "Quantity: ${cartItem.quantity}"
            priceTextView.text = "Price: $${cartItem.product.price * cartItem.quantity}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            onDeleteClick(cartItem.product.id) // Call the delete function with the product ID
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
