package flc.demo.client.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import flc.demo.client.R
import flc.demo.client.network.Product

class ProductSelectionAdapter(
    private var products: MutableList<Product>,
    private val onAddToCartClicked: (Product, Int) -> Unit
) : RecyclerView.Adapter<ProductSelectionAdapter.ProductViewHolder>() {

    private val productQuantities = mutableMapOf<Long, Int>() // Map of product IDs to selected quantities

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.product_name)
        val productPriceTextView: TextView = itemView.findViewById(R.id.product_price)
        val productStockTextView: TextView = itemView.findViewById(R.id.product_stock)
        val incrementButton: Button = itemView.findViewById(R.id.increment_button)
        val addToCartButton: Button = itemView.findViewById(R.id.add_to_cart_button)
        val decrementButton: Button = itemView.findViewById(R.id.decrement_button)
        val productImageView: ImageView = itemView.findViewById(R.id.product_image)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantity_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_selection, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.productNameTextView.text = product.name
        holder.productPriceTextView.text = "Price: $${product.price}"
        holder.productStockTextView.text = "Stock: ${product.stockQuantity}"

        Glide.with(holder.itemView.context)
            .load(product.urlImage) // Assuming `product.urlImage` exists
            .into(holder.productImageView)

        // Set initial quantity
        val currentQuantity = productQuantities[product.id] ?: 1
        holder.quantityTextView.text = currentQuantity.toString()

        holder.incrementButton.setOnClickListener {
            val quantity = productQuantities[product.id] ?: 1
            val newQuantity = (quantity + 1).coerceAtMost(product.stockQuantity)
            productQuantities[product.id] = newQuantity
            holder.quantityTextView.text = newQuantity.toString()
        }

        holder.decrementButton.setOnClickListener {
            val quantity = productQuantities[product.id] ?: 1
            if (quantity > 1) {
                val newQuantity = quantity - 1
                productQuantities[product.id] = newQuantity
                holder.quantityTextView.text = newQuantity.toString()
            }
        }

        holder.addToCartButton.setOnClickListener {
            val quantity = productQuantities[product.id] ?: 1
            onAddToCartClicked(product, quantity)
        }
    }

    override fun getItemCount(): Int = products.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }
}
