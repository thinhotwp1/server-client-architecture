package flc.demo.client

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.adapter.CartAdapter
import flc.demo.client.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : ComponentActivity() {

    private lateinit var cartService: CartService
    private lateinit var orderService: OrderService
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private var cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartService = ApiClient.createService(CartService::class.java)
        orderService = ApiClient.createService(OrderService::class.java) // Initialize OrderService

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

        // Set up checkout button functionality
        checkoutButton.setOnClickListener {
            createOrder()
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

    // Checkout and create an order
    private fun createOrder() {
        val currentUser = UserCurrent.getCurrentUser()

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Prepare order details from cart items
        val orderDetails = cartItems.map { cartItem ->
            OrderDetail(
                productId = cartItem.product.id,
                productName = cartItem.product.name,
                price = cartItem.product.price,
                quantity = cartItem.quantity,
            )
        }

        if (orderDetails.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val orderRequest = OrderRequest(
            username = currentUser.username,
            orderDetails = orderDetails
        )

        // Call the createOrder API
        orderService.createOrder(orderRequest).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() == true) {
                    Toast.makeText(this@CartActivity, "Order created successfully", Toast.LENGTH_SHORT).show()
                    clearCart() // Clear the cart after successful checkout
                } else {
                    Toast.makeText(this@CartActivity, "Failed to create order", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@CartActivity, "Error creating order", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Clear cart after successful order
    private fun clearCart() {
        UserCurrent.getCurrentUser()?.let { currentUser ->
            cartService.clearCart(currentUser.username).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CartActivity, "Cart cleared", Toast.LENGTH_SHORT).show()
                        fetchCartItems() // Refresh cart UI
                    } else {
                        Toast.makeText(this@CartActivity, "Failed to clear cart", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(this@CartActivity, "Error clearing cart", Toast.LENGTH_SHORT).show()
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
