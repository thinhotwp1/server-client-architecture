package demo.com.server.controller;

import demo.com.server.entity.Category;
import demo.com.server.rest.CategoryDeleteRequest;
import demo.com.server.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (SQLException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            if (category != null) {
                return ResponseEntity.ok(category);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/create")
    public Object createCategory(@RequestBody Category category) {
        try {
            return categoryService.createCategory(category);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PostMapping("/update")
    public Object updateCategory(@RequestBody Category category) {
        try {
            return categoryService.updateCategory(category);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PostMapping("/delete")
    public Object deleteCategory(@RequestBody CategoryDeleteRequest request) {
        try {
            return categoryService.deleteCategory(request.getId());
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
