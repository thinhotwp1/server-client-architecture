package flc.demo.client.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

data class Order(
    val orderId: Long,
    val username: String,
    val orderDate: String,
    val status: String,
    val total: Double
)

data class OrderDetail(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Double
)

data class OrderRequest(
    val username: String,
    val orderDetails: List<OrderDetail>
)

data class UpdateOrderStatus(
    val orderId: Long,
    val status: String
)

interface OrderService {

    // Create order
    @POST("/orders/create")
    fun createOrder(@Body orderRequest: OrderRequest): Call<Boolean>

    @GET("/orders/user/{username}")
    suspend fun getOrdersByUsername(@Path("username") username: String): List<Order>

    @GET("/orders")
    suspend fun getAllOrders(): List<Order>

    @GET("/orders/details/{orderId}")
    suspend fun getOrderDetailsByOrderId(@Path("orderId") orderId: Long): List<OrderDetail>

    @POST("/orders/updateStatus")
    suspend fun updateOrderStatus(@Body request: UpdateOrderStatus): Response<Boolean>

}
