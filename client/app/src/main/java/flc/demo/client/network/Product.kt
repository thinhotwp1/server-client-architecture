package flc.demo.client.network

import retrofit2.Call
import retrofit2.http.*

data class Product(
    val id: Long,
    val name: String,
    val urlImage: String,
    val price: Double,
    val stockQuantity: Int,
    val category: Category
)

data class ProductDeleteRequest(val id: Long)

interface ProductService {
    @GET("/products/search-by-category")
    fun searchProductsByCategory(@Query("categoryId") categoryId: Long): Call<List<Product>>

    @GET("/products/search")
    fun searchProductsByName(@Query("name") name: String): Call<List<Product>>

    @POST("/products/create")
    fun createProduct(@Body product: Product): Call<Boolean>

    @POST("/products/update")
    fun updateProduct(@Body product: Product): Call<Boolean>

    @POST("/products/delete")
    fun deleteProduct(@Body request: ProductDeleteRequest): Call<Boolean>
}
