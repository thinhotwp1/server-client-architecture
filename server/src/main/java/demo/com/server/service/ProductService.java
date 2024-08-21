package demo.com.server.service;

import demo.com.server.config.SQLiteConnection;
import demo.com.server.config.UserCurrent;
import demo.com.server.entity.Category;
import demo.com.server.entity.Product;
import demo.com.server.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class ProductService {

    @PostConstruct
    public void init() {
        try {
            createTableIfNotExists();  // Gọi tạo bảng khi khởi động
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Phương thức tạo bảng
    public void createTableIfNotExists() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS categories (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    category_name TEXT NOT NULL\n" +
                ");\n" +
                "\n";
        String createProductTable = "CREATE TABLE IF NOT EXISTS products (\n" +
                        "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "    name TEXT NOT NULL,\n" +
                        "    price REAL NOT NULL,\n" +
                        "    stock_quantity INTEGER NOT NULL,\n" +
                        "    category_id INTEGER NOT NULL,\n" +
                        "    url_image TEXT,\n" + // Thêm cột url_image
                        "    FOREIGN KEY(category_id) REFERENCES categories(id)\n" +
                        ")";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createProductTable);
        }
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.id AS category_id, c.category_name FROM products p " +
                "JOIN categories c ON p.category_id = c.id";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setUrlImage(rs.getString("url_image")); // Lấy urlImage

                // Lấy thông tin Category
                Category category = new Category();
                category.setId(rs.getLong("category_id"));
                category.setCategoryName(rs.getString("category_name"));
                product.setCategory(category);

                products.add(product);
            }
        }
        return products;
    }

    public Product getProductById(int id) throws SQLException {
        Product product = null;
        String sql = "SELECT p.*, c.id AS category_id, c.category_name FROM products p " +
                "JOIN categories c ON p.category_id = c.id WHERE p.id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    product.setUrlImage(rs.getString("url_image")); // Lấy urlImage

                    // Lấy thông tin Category
                    Category category = new Category();
                    category.setId(rs.getLong("category_id"));
                    category.setCategoryName(rs.getString("category_name"));
                    product.setCategory(category);
                }
            }
        }
        return product;
    }

    public boolean createProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, price, stock_quantity, category_id, url_image) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getPrice());
            pstmt.setInt(3, product.getStockQuantity());
            pstmt.setLong(4, product.getCategory().getId());
            pstmt.setString(5, product.getUrlImage()); // Thêm urlImage
            pstmt.executeUpdate();

        } catch (Exception e) {
            log.error("Error creating product", e);
            return false;
        }
        return true;
    }

    public boolean updateProduct(Product product) throws SQLException {
        User currentUser = UserCurrent.getCurrentUser();

        if (currentUser == null || currentUser.getRole() != 0) { // 0 là admin
            throw new SecurityException("Access denied. Admin role is required.");
        }
        String sql = "UPDATE products SET name = ?, price = ?, stock_quantity = ?, category_id = ?, url_image = ? WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getPrice());
            pstmt.setInt(3, product.getStockQuantity());
            pstmt.setLong(4, product.getCategory().getId()); // cập nhật category_id
            pstmt.setString(5, product.getUrlImage()); // Cập nhật urlImage
            pstmt.setLong(6, product.getId()); // Sử dụng id kiểu Long
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error updating product", e);
            return false;
        }
        return true;
    }

    public boolean deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error deleting product", e);
            return false;
        }
        return true;
    }

    public List<Product> searchProductsByName(String name) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.id AS category_id, c.category_name FROM products p " +
                "JOIN categories c ON p.category_id = c.id WHERE p.name LIKE ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    product.setUrlImage(rs.getString("url_image")); // Lấy urlImage

                    // Lấy thông tin Category
                    Category category = new Category();
                    category.setId(rs.getLong("category_id"));
                    category.setCategoryName(rs.getString("category_name"));
                    product.setCategory(category);

                    products.add(product);
                }
            }
        }
        return products;
    }

    public List<Product> filterProductsByPrice(double minPrice, double maxPrice) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.id AS category_id, c.category_name FROM products p " +
                "JOIN categories c ON p.category_id = c.id WHERE p.price BETWEEN ? AND ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, minPrice);
            pstmt.setDouble(2, maxPrice);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    product.setUrlImage(rs.getString("url_image")); // Lấy urlImage

                    // Lấy thông tin Category
                    Category category = new Category();
                    category.setId(rs.getLong("category_id"));
                    category.setCategoryName(rs.getString("category_name"));
                    product.setCategory(category);

                    products.add(product);
                }
            }
        }
        return products;
    }

    public List<Product> searchProductsByCategoryId(long categoryId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.id AS category_id, c.category_name FROM products p " +
                "JOIN categories c ON p.category_id = c.id WHERE c.id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    product.setUrlImage(rs.getString("url_image")); // Lấy urlImage

                    // Lấy thông tin Category
                    Category category = new Category();
                    category.setId(rs.getLong("category_id"));
                    category.setCategoryName(rs.getString("category_name"));
                    product.setCategory(category);

                    products.add(product);
                }
            }
        }
        return products;
    }
}
