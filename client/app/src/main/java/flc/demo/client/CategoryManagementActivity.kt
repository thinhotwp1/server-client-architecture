package flc.demo.client

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flc.demo.client.adapter.CategoryAdapter
import flc.demo.client.network.ApiClient
import flc.demo.client.network.Category
import flc.demo.client.network.CategoryDeleteRequest
import flc.demo.client.network.CategoryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryManagementActivity : ComponentActivity() {

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryService: CategoryService
    private var selectedCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_management)

        // Initialize Retrofit Service
        categoryService = ApiClient.createService(CategoryService::class.java)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_categories)
        categoryAdapter = CategoryAdapter(mutableListOf()) { category ->
            selectedCategory = category
        }
        recyclerView.adapter = categoryAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch and display categories
        fetchCategories()

        // Set up button listeners
        findViewById<Button>(R.id.add_category_button).setOnClickListener {
            showCategoryDialog(null)
        }

        findViewById<Button>(R.id.edit_category_button).setOnClickListener {
            selectedCategory?.let {
                showCategoryDialog(it)
            } ?: run {
                Toast.makeText(this, "Please select a category to edit", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.delete_category_button).setOnClickListener {
            selectedCategory?.let {
                deleteCategory(it.id)
            } ?: run {
                Toast.makeText(this, "Please select a category to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCategories() {
        categoryService.getAllCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        categoryAdapter.updateCategories(categories)
                    }
                } else {
                    Toast.makeText(this@CategoryManagementActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Log.e("CategoryManagement", "Error fetching categories", t)
            }
        })
    }

    private fun showCategoryDialog(category: Category?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_category, null)
        val editTextCategoryName = dialogView.findViewById<EditText>(R.id.edit_text_category_name)

        category?.let {
            editTextCategoryName.setText(it.categoryName)
        }

        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle(if (category == null) "Add Category" else "Edit Category")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val categoryName = editTextCategoryName.text.toString()
                if (categoryName.isNotEmpty()) {
                    if (category == null) {
                        addCategory(categoryName)
                    } else {
                        updateCategory(Category(category.id, categoryName))
                    }
                }
            }
            .setNegativeButton("Cancel", null)

        dialogBuilder.create().show()
    }

    private fun addCategory(categoryName: String) {
        val newCategory = Category(0, categoryName)
        categoryService.createCategory(newCategory).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() == true) {
                    fetchCategories() // Refresh list
                    Toast.makeText(this@CategoryManagementActivity, "Category added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CategoryManagementActivity, "Failed to add category", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e("CategoryManagement", "Error adding category", t)
            }
        })
    }

    private fun updateCategory(updatedCategory: Category) {
        categoryService.updateCategory(updatedCategory).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() == true) {
                    fetchCategories() // Refresh list
                    Toast.makeText(this@CategoryManagementActivity, "Category updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CategoryManagementActivity, "Failed to update category", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e("CategoryManagement", "Error updating category", t)
            }
        })
    }

    private fun deleteCategory(categoryId: Long) {
        val request = CategoryDeleteRequest(categoryId)
        categoryService.deleteCategory(request).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() == true) {
                    fetchCategories() // Refresh list
                    Toast.makeText(this@CategoryManagementActivity, "Category deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CategoryManagementActivity, "Failed to delete category", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e("CategoryManagement", "Error deleting category", t)
            }
        })
    }
}
