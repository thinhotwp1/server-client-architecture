package flc.demo.client

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.adapter.ProductSelectionAdapter
import flc.demo.client.network.*
import kotlinx.coroutines.*
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

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

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
        productSelectionAdapter = ProductSelectionAdapter(mutableListOf(), onAddToCartClicked = { product, quantity ->
            selectedProducts[product] = quantity
            addSelectedProductsToCart()
        })
        recyclerView.adapter = productSelectionAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch and set up categories asynchronously
        coroutineScope.launch { fetchCategories() }

        searchButton.setOnClickListener {
            coroutineScope.launch { searchProductsByName(searchEditText.text.toString()) }
        }
    }

    private suspend fun fetchCategories() {
        withContext(Dispatchers.IO) {
            val response: Response<List<Category>> = categoryService.getAllCategories().execute()
            if (response.isSuccessful) {
                categories = response.body() ?: emptyList()
                val categoryNames = categories.map { it.categoryName }
                withContext(Dispatchers.Main) {
                    setupCategorySpinner(categoryNames)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductSelectionActivity, "Failed to fetch categories", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun searchProductsByName(query: String) {
        withContext(Dispatchers.IO) {
            val response: Response<List<Product>> = productService.searchProductsByName(query).execute()
            if (response.isSuccessful) {
                val products = response.body() ?: emptyList()
                withContext(Dispatchers.Main) {
                    productSelectionAdapter.updateProducts(products)
                }
            }
        }
    }

    private suspend fun fetchProductsForCategory(categoryId: Long) {
        withContext(Dispatchers.IO) {
            val response: Response<List<Product>> = productService.searchProductsByCategory(categoryId).execute()
            if (response.isSuccessful) {
                val products = response.body() ?: emptyList()
                withContext(Dispatchers.Main) {
                    productSelectionAdapter.updateProducts(products)
                }
            }
        }
    }

    private fun setupCategorySpinner(categoryNames: List<String>) {
        val adapter = object : ArrayAdapter<String>(
            this@ProductSelectionActivity,
            R.layout.spinner_item,
            categoryNames
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).setTextColor(Color.WHITE)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).setTextColor(Color.BLACK)
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                coroutineScope.launch { fetchProductsForCategory(selectedCategory.id) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun addSelectedProductsToCart() {
        selectedProducts.forEach { (product, quantity) ->
            UserCurrent.getCurrentUser()?.let { user ->
                coroutineScope.launch(Dispatchers.IO) {
                    val response = cartService.addProductToCart(user.username, product.id, quantity).execute()
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() == true) {
                            Toast.makeText(this@ProductSelectionActivity, "${product.name} added to cart!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@ProductSelectionActivity, "Failed to add ${product.name} to cart", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        selectedProducts.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
