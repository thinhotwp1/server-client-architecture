package flc.demo.client

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.adapter.ProductAdapter
import flc.demo.client.network.ApiClient
import flc.demo.client.network.Category
import flc.demo.client.network.CategoryService
import flc.demo.client.network.Product
import flc.demo.client.network.ProductService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductManagementActivity : ComponentActivity() {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productService: ProductService
    private lateinit var categoryService: CategoryService

    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_management)

        productService = ApiClient.createService(ProductService::class.java)
        categoryService = ApiClient.createService(CategoryService::class.java)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_products)
        productAdapter = ProductAdapter(mutableListOf())
        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchEditText = findViewById(R.id.search_product_edit_text)

        // Fetch products by category
        fetchProductsByCategories()

        findViewById<Button>(R.id.add_product_button).setOnClickListener {
            // Handle adding a product
        }

        // Set up search functionality
        searchEditText.setOnEditorActionListener { v, actionId, event ->
            searchProductsByName(searchEditText.text.toString())
            true
        }
    }

    private fun fetchProductsByCategories() {
        // Fetch all categories first
        categoryService.getAllCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        val categoryIds = categories.map { it.id } // Extract category IDs

                        // Now fetch products for each category ID
                        fetchProductsForCategoryIds(categoryIds)
                    }
                } else {
                    Toast.makeText(this@ProductManagementActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Log.e("ProductManagement", "Error fetching categories", t)
            }
        })
    }

    private fun fetchProductsForCategoryIds(categoryIds: List<Long>) {
        val allProducts = mutableListOf<Product>()
        val productCalls = categoryIds.map { categoryId ->
            productService.searchProductsByCategory(categoryId)
        }

        // Make multiple API calls to fetch products for all category IDs
        productCalls.forEachIndexed { index, call ->
            call.enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    if (response.isSuccessful) {
                        response.body()?.let { products ->
                            allProducts.addAll(products)
                            if (index == productCalls.size - 1) { // Last call
                                // Update the adapter with all products once all calls are complete
                                productAdapter.updateProducts(allProducts)
                            }
                        }
                    } else {
                        Toast.makeText(this@ProductManagementActivity, "Failed to load products", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    Log.e("ProductManagement", "Error fetching products", t)
                }
            })
        }
    }

    private fun searchProductsByName(name: String) {
        productService.searchProductsByName(name).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        productAdapter.updateProducts(products)
                    }
                } else {
                    Toast.makeText(this@ProductManagementActivity, "Failed to search products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("ProductManagement", "Error searching products", t)
            }
        })
    }
}
