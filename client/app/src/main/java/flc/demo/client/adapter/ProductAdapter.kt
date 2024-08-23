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

class ProductAdapter(
    private var products: MutableList<Product>,
    private val onEditClicked: (Product) -> Unit,
    private val onDeleteClicked: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.product_name)
        val categoryNameTextView: TextView = itemView.findViewById(R.id.category_name)
        val productPriceTextView: TextView = itemView.findViewById(R.id.product_price)
        val productStockTextView: TextView = itemView.findViewById(R.id.product_stock)
        val productImageView: ImageView = itemView.findViewById(R.id.product_image)
        val editButton: Button = itemView.findViewById(R.id.edit_product_button)
        val deleteButton: Button = itemView.findViewById(R.id.delete_product_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.productNameTextView.text = product.name
        holder.categoryNameTextView.text = product.category.categoryName // Set the category name
        holder.productPriceTextView.text = "Price: $${product.price}"
        holder.productStockTextView.text = "Stock: ${product.stockQuantity}"

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(product.urlImage)
            .into(holder.productImageView)

        // Handle edit and delete button clicks
        holder.editButton.setOnClickListener { onEditClicked(product) }
        holder.deleteButton.setOnClickListener { onDeleteClicked(product) }
    }

    override fun getItemCount(): Int = products.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }
}
