package demo.com.server.controller;

import demo.com.server.entity.Product;
import demo.com.server.rest.ProductDeleteRequest;
import demo.com.server.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() throws SQLException {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable int id) throws SQLException {
        return productService.getProductById(id);
    }

    @PostMapping("/create")
    public boolean createProduct(@RequestBody Product product) throws SQLException {
        return productService.createProduct(product);
    }

    @PostMapping("/update")
    public boolean updateProduct(@RequestBody Product product) throws SQLException {
        return productService.updateProduct(product);
    }

    @PostMapping("/delete")
    public boolean deleteProduct(@RequestBody ProductDeleteRequest request) throws SQLException {
        return productService.deleteProduct(request.getId());
    }
}
