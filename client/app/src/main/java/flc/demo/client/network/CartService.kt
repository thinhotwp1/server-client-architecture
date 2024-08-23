package flc.demo.client.network

import retrofit2.Call
import retrofit2.http.*

data class Cart(
    val userName: String,
    val cartItems: List<CartItem>
)
data class CartItem(
    val product: Product,
    var quantity: Int
)

interface CartService {

    @GET("/cart/{userName}")
    fun getCartByUserName(@Path("userName") userName: String): Call<Cart>

    @POST("/cart/add")
    fun addProductToCart(
        @Query("userName") userName: String,
        @Query("productId") productId: Long,
        @Query("quantity") quantity: Int
    ): Call<Boolean>

    @POST("/cart/update")
    fun updateCart(
        @Query("userName") userName: String,
        @Query("productId") productId: Long,
        @Query("quantity") quantity: Int
    ): Call<Boolean>

    @POST("/cart/remove")
    fun removeProductFromCart(
        @Query("userName") userName: String,
        @Query("productId") productId: Long
    ): Call<Boolean>

    @POST("/cart/clear")
    fun clearCart(@Query("userName") userName: String): Call<Boolean>
}
