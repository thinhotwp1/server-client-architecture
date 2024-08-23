package flc.demo.client

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.adapter.ProductAdapter
import flc.demo.client.network.ApiClient
import flc.demo.client.network.Category
import flc.demo.client.network.CategoryService
import flc.demo.client.network.Product
import flc.demo.client.network.ProductDeleteRequest
import flc.demo.client.network.ProductService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductManagementActivity : ComponentActivity() {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productService: ProductService
    private lateinit var categoryService: CategoryService

    private lateinit var searchEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var searchButton: Button
    private lateinit var addProductButton: Button

    private lateinit var categories: List<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_management)

        productService = ApiClient.createService(ProductService::class.java)
        categoryService = ApiClient.createService(CategoryService::class.java)

        searchEditText = findViewById(R.id.search_product_edit_text)
        categorySpinner = findViewById(R.id.category_spinner)
        searchButton = findViewById(R.id.search_button)
        addProductButton = findViewById(R.id.add_product_button)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_products)
        productAdapter = ProductAdapter(mutableListOf(), onEditClicked = { product ->
            showEditProductDialog(product)
        }, onDeleteClicked = { product ->
            deleteProduct(product)
        })
        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchCategories()

        searchButton.setOnClickListener {
            searchProductsByName(searchEditText.text.toString())
        }

        addProductButton.setOnClickListener {
            showAddProductDialog()
        }
    }
    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_product, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.product_name_edit_text)
        val priceEditText = dialogView.findViewById<EditText>(R.id.product_price_edit_text)
        val stockEditText = dialogView.findViewById<EditText>(R.id.product_stock_edit_text)
        val imageEditText = dialogView.findViewById<EditText>(R.id.product_image_edit_text)

        AlertDialog.Builder(this)
            .setTitle("Add Product")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameEditText.text.toString()
                val price = priceEditText.text.toString().toDouble()
                val stock = stockEditText.text.toString().toInt()
                val imageUrl = imageEditText.text.toString()
                val selectedCategory = categories[categorySpinner.selectedItemPosition]
                val newProduct = Product(
                    id = 0, // New product, so id is 0
                    name = name,
                    urlImage = imageUrl,
                    price = price,
                    stockQuantity = stock,
                    category = selectedCategory
                )
                addProduct(newProduct)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun showEditProductDialog(product: Product) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_product, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.product_name_edit_text)
        val priceEditText = dialogView.findViewById<EditText>(R.id.product_price_edit_text)
        val stockEditText = dialogView.findViewById<EditText>(R.id.product_stock_edit_text)
        val imageEditText = dialogView.findViewById<EditText>(R.id.product_image_edit_text)

        nameEditText.setText(product.name)
        priceEditText.setText(product.price.toString())
        stockEditText.setText(product.stockQuantity.toString())
        imageEditText.setText(product.urlImage)

        AlertDialog.Builder(this)
            .setTitle("Edit Product")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = nameEditText.text.toString()
                val price = priceEditText.text.toString().toDouble()
                val stock = stockEditText.text.toString().toInt()
                val imageUrl = imageEditText.text.toString()
                val updatedProduct = product.copy(
                    name = name,
                    price = price,
                    stockQuantity = stock,
                    urlImage = imageUrl
                )
                updateProduct(updatedProduct)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addProduct(product: Product) {
        productService.createProduct(product).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() == true) {
                    Toast.makeText(this@ProductManagementActivity, "Product added successfully", Toast.LENGTH_SHORT).show()
                    fetchProductsForCategory(categories[categorySpinner.selectedItemPosition].id)
                } else {
                    Toast.makeText(this@ProductManagementActivity, "Failed to add product", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Toast.makeText(this@ProductManagementActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteProduct(product: Product) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Delete") { _, _ ->
                productService.deleteProduct(ProductDeleteRequest(product.id)).enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful && response.body() == true) {
                            Toast.makeText(this@ProductManagementActivity, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                            fetchProductsForCategory(categories[categorySpinner.selectedItemPosition].id)
                        } else {
                            Toast.makeText(this@ProductManagementActivity, "Failed to delete product", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        Toast.makeText(this@ProductManagementActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun updateProduct(product: Product) {
        productService.updateProduct(product).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() == true) {
                    Toast.makeText(this@ProductManagementActivity, "Product updated successfully", Toast.LENGTH_SHORT).show()
                    fetchProductsForCategory(categories[categorySpinner.selectedItemPosition].id)
                } else {
                    Toast.makeText(this@ProductManagementActivity, "Failed to update product", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Toast.makeText(this@ProductManagementActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun fetchCategories() {
        categoryService.getAllCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    categories = response.body() ?: emptyList()
                    val categoryNames = categories.map { it.categoryName }

                    // Create an ArrayAdapter with a custom layout
                    val adapter = object : ArrayAdapter<String>(this@ProductManagementActivity, R.layout.spinner_item, categoryNames) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getView(position, convertView, parent)
                            (view as TextView).setTextColor(Color.WHITE) // Set text color for selected item
                            return view
                        }

                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getDropDownView(position, convertView, parent)
                            (view as TextView).setTextColor(Color.BLACK) // Set text color for dropdown items
                            return view
                        }
                    }

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categorySpinner.adapter = adapter

                    categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val selectedCategory = categories[position]
                            fetchProductsForCategory(selectedCategory.id)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                } else {
                    Toast.makeText(this@ProductManagementActivity, "Failed to fetch categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@ProductManagementActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchProductsByName(query: String) {
        productService.searchProductsByName(query).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    productAdapter.updateProducts(products)
                } else {
                    Toast.makeText(this@ProductManagementActivity, "Failed to search products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@ProductManagementActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchProductsForCategory(categoryId: Long) {
        productService.searchProductsByCategory(categoryId).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    productAdapter.updateProducts(products)
                } else {
                    Toast.makeText(this@ProductManagementActivity, "Failed to fetch products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@ProductManagementActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
