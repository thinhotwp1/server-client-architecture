package demo.com.server.service;

import demo.com.server.config.SQLiteConnection;
import demo.com.server.entity.Cart;
import demo.com.server.entity.Product;
import demo.com.server.entity.CartItem;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class CartService {

    @PostConstruct
    public void init() throws SQLException {
        createTablesIfNotExist();
    }

    public void createTablesIfNotExist() throws SQLException {
        String createCartTableSQL = "CREATE TABLE IF NOT EXISTS cart (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    user_id INTEGER NOT NULL,\n" +
                "    product_id INTEGER NOT NULL,\n" +
                "    quantity INTEGER NOT NULL,\n" +
                "    FOREIGN KEY (product_id) REFERENCES products(id),\n" +
                "    FOREIGN KEY (user_id) REFERENCES users(id),\n" +
                "    UNIQUE(user_id, product_id)\n" +
                ")";
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createCartTableSQL);
        }
    }

    public Cart getCartByUserName(String userName) throws SQLException {
        Cart cart = new Cart();
        cart.setUserName(userName);
        List<CartItem> cartItems = new ArrayList<>();

        String sql = "SELECT p.*, c.quantity FROM cart c JOIN products p ON c.product_id = p.id WHERE c.user_name = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    product.setUrlImage(rs.getString("url_image"));

                    int quantity = rs.getInt("quantity");
                    cartItems.add(new CartItem(product, quantity));
                }
            }
        }

        cart.setCartItems(cartItems);
        return cart;
    }

    public boolean addProductToCart(String userName, Long productId, int quantity) throws SQLException {
        String sql = "INSERT INTO cart (user_name, product_id, quantity) VALUES (?, ?, ?) ON CONFLICT(user_name, product_id) DO UPDATE SET quantity = quantity + ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setLong(2, productId);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, quantity); // For updating the quantity
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error adding product to cart", e);
            return false;
        }
        return true;
    }

    public boolean updateCart(String userName, Long productId, int quantity) throws SQLException {
        String sql = "UPDATE cart SET quantity = ? WHERE user_name = ? AND product_id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setString(2, userName);
            pstmt.setLong(3, productId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error updating cart", e);
            return false;
        }
        return true;
    }

    public boolean removeProductFromCart(String userName, Long productId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_name = ? AND product_id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setLong(2, productId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error removing product from cart", e);
            return false;
        }
        return true;
    }

    public boolean clearCart(String userName) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_name = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error clearing cart", e);
            return false;
        }
        return true;
    }
}
