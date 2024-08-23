package flc.demo.client

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.adapter.CartAdapter
import flc.demo.client.network.ApiClient
import flc.demo.client.network.Cart
import flc.demo.client.network.CartItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import flc.demo.client.network.CartService
import flc.demo.client.network.UserCurrent

class CartActivity : ComponentActivity() {

    private lateinit var cartService: CartService
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private var cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartService = ApiClient.createService(CartService::class.java)
        recyclerView = findViewById(R.id.recycler_view_cart_items)
        val checkoutButton: Button = findViewById(R.id.order_button)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Pass the delete action to the adapter
        cartAdapter = CartAdapter(cartItems) { productId ->
            deleteCartItem(productId)
        }
        recyclerView.adapter = cartAdapter

        // Fetch and display cart items
        fetchCartItems()

        // Set up checkout button
        checkoutButton.setOnClickListener {
            Toast.makeText(this, "Checkout feature not yet implemented", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchCartItems() {
        UserCurrent.getCurrentUser()?.let {
            cartService.getCartByUserName(it.username).enqueue(object : Callback<Cart> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<Cart>, response: Response<Cart>) {
                    if (response.isSuccessful) {
                        val cart = response.body()
                        cartItems.clear()
                        cart?.cartItems?.forEach { (product, quantity) ->
                            cartItems.add(CartItem(product, quantity))
                        }
                        cartAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@CartActivity, "Failed to load cart", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Cart>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(this@CartActivity, "Failed to load cart", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun deleteCartItem(productId: Long) {
        UserCurrent.getCurrentUser()?.let { currentUser ->
            cartService.removeProductFromCart(currentUser.username, productId)
                .enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@CartActivity, "Item removed", Toast.LENGTH_SHORT).show()
                            fetchCartItems() // Refresh cart after item is removed
                        } else {
                            Toast.makeText(this@CartActivity, "Failed to remove item", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(this@CartActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}

