package demo.com.server.service;

import demo.com.server.config.SQLiteConnection;
import demo.com.server.entity.Order;
import demo.com.server.entity.OrderDetail;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class OrderService {
    @PostConstruct
    private void init() throws SQLException {
        createTablesIfNotExist();
    }

    public void createTablesIfNotExist() throws SQLException {
        String createOrderTableSQL = "CREATE TABLE IF NOT EXISTS orders (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "order_date TEXT NOT NULL, " +
                "status TEXT NOT NULL" +
                ");";

        String createOrderDetailTableSQL = "CREATE TABLE IF NOT EXISTS order_details (" +
                "order_detail_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "price REAL NOT NULL, " +
                "FOREIGN KEY(order_id) REFERENCES orders(order_id)" +
                ");";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createOrderTableSQL);
            stmt.execute(createOrderDetailTableSQL);
        }
    }

    public boolean createOrder(Order order, List<OrderDetail> orderDetails) throws SQLException {
        String insertOrderSQL = "INSERT INTO orders(username, order_date, status) VALUES(?, ?, ?)";
        String insertOrderDetailSQL = "INSERT INTO order_details(order_id, product_id, quantity, price) VALUES(?, ?, ?, ?)";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmtOrder = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstmtOrderDetail = conn.prepareStatement(insertOrderDetailSQL)) {

            // Insert order
            pstmtOrder.setString(1, order.getUsername());
            pstmtOrder.setString(2, order.getOrderDate().toString());
            pstmtOrder.setString(3, order.getStatus());
            int affectedRows = pstmtOrder.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            // Get generated order ID
            try (ResultSet generatedKeys = pstmtOrder.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long orderId = generatedKeys.getLong(1);

                    // Insert order details
                    for (OrderDetail detail : orderDetails) {
                        pstmtOrderDetail.setLong(1, orderId);
                        pstmtOrderDetail.setLong(2, detail.getProductId());
                        pstmtOrderDetail.setInt(3, detail.getQuantity());
                        pstmtOrderDetail.setDouble(4, detail.getPrice());
                        pstmtOrderDetail.addBatch();
                    }
                    pstmtOrderDetail.executeBatch();
                }
            }
        } catch (Exception e) {
            log.error("Error creating order", e);
            return false;
        }
        return true;
    }

    public Order getOrderById(Long orderId) throws SQLException {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    order = new Order();
                    order.setOrderId(rs.getLong("order_id"));
                    order.setUsername(rs.getString("username"));
                    order.setOrderDate(LocalDateTime.parse(rs.getString("order_date")));
                    order.setStatus(rs.getString("status"));
                }
            }
        }
        return order;
    }

    public List<Order> getOrdersByUsername(String username) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE username = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getLong("order_id"));
                    order.setUsername(rs.getString("username"));
                    order.setOrderDate(LocalDateTime.parse(rs.getString("order_date")));
                    order.setStatus(rs.getString("status"));
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public boolean updateOrderStatus(Long orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setLong(2, orderId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error updating order status", e);
            return false;
        }
        return true;
    }

    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) throws SQLException {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE order_id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrderDetailId(rs.getLong("order_detail_id"));
                    detail.setOrderId(rs.getLong("order_id"));
                    detail.setProductId(rs.getLong("product_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setPrice(rs.getDouble("price"));
                    orderDetails.add(detail);
                }
            }
        }
        return orderDetails;
    }
}
