package flc.demo.client.network

import retrofit2.Call
import retrofit2.http.*

data class Category(val id: Long, val categoryName: String)
data class CategoryDeleteRequest(val id: Long)

interface CategoryService {
    @GET("/categories")
    fun getAllCategories(): Call<List<Category>>

    @GET("/categories/{id}")
    fun getCategoryById(@Path("id") id: Long): Call<Category>

    @POST("/categories/create")
    fun createCategory(@Body category: Category): Call<Boolean>

    @POST("/categories/update")
    fun updateCategory(@Body category: Category): Call<Boolean>

    @POST("/categories/delete")
    fun deleteCategory(@Body request: CategoryDeleteRequest): Call<Boolean>
}
