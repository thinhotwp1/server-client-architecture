package flc.demo.client

import android.app.AlertDialog
import android.content.Intent
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
import flc.demo.client.adapter.ProductSelectionAdapter
import flc.demo.client.network.ApiClient
import flc.demo.client.network.CartService
import flc.demo.client.network.Category
import flc.demo.client.network.CategoryService
import flc.demo.client.network.Product
import flc.demo.client.network.ProductDeleteRequest
import flc.demo.client.network.ProductService
import flc.demo.client.network.UserCurrent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductSelectionActivity : ComponentActivity() {

    private lateinit var productSelectionAdapter: ProductSelectionAdapter
    private lateinit var productService: ProductService
    private lateinit var cartService: CartService
    private lateinit var categoryService: CategoryService

    private lateinit var searchEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var searchButton: Button

    private lateinit var categories: List<Category>

    private var selectedProducts = mutableMapOf<Product, Int>() // Products to add to the cart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_selection)

        productService = ApiClient.createService(ProductService::class.java)
        cartService = ApiClient.createService(CartService::class.java)
        categoryService = ApiClient.createService(CategoryService::class.java)

        searchEditText = findViewById(R.id.search_product_edit_text)
        categorySpinner = findViewById(R.id.category_spinner)
        searchButton = findViewById(R.id.search_button)

        val viewCartButton: Button = findViewById(R.id.view_cart_button)
        viewCartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_products)
        productSelectionAdapter =
            ProductSelectionAdapter(mutableListOf(), onAddToCartClicked = { product, quantity ->
                // Add product to selectedProducts map
                selectedProducts[product] = quantity
                addSelectedProductsToCart()
            })
        recyclerView.adapter = productSelectionAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch and set up categories
        fetchCategories()

        searchButton.setOnClickListener {
            searchProductsByName(searchEditText.text.toString())
        }
    }


    private fun fetchCategories() {
        categoryService.getAllCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(
                call: Call<List<Category>>,
                response: Response<List<Category>>
            ) {
                if (response.isSuccessful) {
                    categories = response.body() ?: emptyList()
                    val categoryNames = categories.map { it.categoryName }

                    // Create an ArrayAdapter with a custom layout
                    val adapter = object : ArrayAdapter<String>(
                        this@ProductSelectionActivity,
                        R.layout.spinner_item,
                        categoryNames
                    ) {
                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val view = super.getView(position, convertView, parent)
                            (view as TextView).setTextColor(Color.WHITE) // Set text color for selected item
                            return view
                        }

                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val view = super.getDropDownView(position, convertView, parent)
                            (view as TextView).setTextColor(Color.BLACK) // Set text color for dropdown items
                            return view
                        }
                    }

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categorySpinner.adapter = adapter

                    categorySpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedCategory = categories[position]
                                fetchProductsForCategory(selectedCategory.id)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                } else {
                    Toast.makeText(
                        this@ProductSelectionActivity,
                        "Failed to fetch categories",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(
                    this@ProductSelectionActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun searchProductsByName(query: String) {
        productService.searchProductsByName(query).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    productSelectionAdapter.updateProducts(products)
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {}
        })
    }

    private fun fetchProductsForCategory(categoryId: Long) {
        productService.searchProductsByCategory(categoryId)
            .enqueue(object : Callback<List<Product>> {
                override fun onResponse(
                    call: Call<List<Product>>,
                    response: Response<List<Product>>
                ) {
                    if (response.isSuccessful) {
                        val products = response.body() ?: emptyList()
                        productSelectionAdapter.updateProducts(products)
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {}
            })
    }

    private fun addSelectedProductsToCart() {
        selectedProducts.forEach { (product, quantity) ->
            UserCurrent.getCurrentUser()?.let {
                cartService.addProductToCart(it.username, product.id, quantity)
                    .enqueue(object : Callback<Boolean> {
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            if (response.isSuccessful && response.body() == true) {
                                // Successfully added product to cart
                                Toast.makeText(
                                    this@ProductSelectionActivity,
                                    "${product.name} added to cart!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Failed to add product to cart
                                Toast.makeText(
                                    this@ProductSelectionActivity,
                                    "Failed to add ${product.name} to cart",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            // Network or server error
                            Toast.makeText(
                                this@ProductSelectionActivity,
                                "Error adding ${product.name} to cart",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

        // Clear selected products after adding to cart
        selectedProducts.clear()
    }
}
