package demo.com.server.service;

import demo.com.server.config.SQLiteConnection;
import demo.com.server.entity.Product;
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
        String createTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "stock_quantity INTEGER NOT NULL" +
                ");";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                products.add(product);
            }
        }
        return products;
    }

    public Product getProductById(int id) throws SQLException {
        Product product = null;
        String sql = "SELECT * FROM products WHERE id = ?";

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
                }
            }
        }
        return product;
    }

    public boolean createProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products(name, price, stock_quantity) VALUES(?, ?, ?)";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getPrice());
            pstmt.setInt(3, product.getStockQuantity());
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error creating product", e);
            return false;
        }
        return true;
    }

    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, price = ?, stock_quantity = ? WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getPrice());
            pstmt.setInt(3, product.getStockQuantity());
            pstmt.setInt(4, Math.toIntExact(product.getId()));
            pstmt.executeUpdate();
        }catch (Exception e) {
            log.error("Error creating product", e);
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
        }catch (Exception e) {
            log.error("Error creating product", e);
            return false;
        }
        return true;
    }

}
