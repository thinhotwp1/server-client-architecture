package demo.com.server.service;

import demo.com.server.config.SQLiteConnection;
import demo.com.server.entity.Order;
import demo.com.server.entity.OrderDetail;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class OrderService {

    @Autowired
    ProductService productService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @PostConstruct
    public void init() throws SQLException {
        createTablesIfNotExist();
    }

    public void createTablesIfNotExist() throws SQLException {
        String createOrderTableSQL = "CREATE TABLE IF NOT EXISTS orders (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "order_date TEXT NOT NULL, " +
                "status TEXT NOT NULL," +
                "total REAL NOT NULL " +
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
        String insertOrderSQL = "INSERT INTO orders(username, order_date, status, total) VALUES(?, ?, ?, ?)";
        String insertOrderDetailSQL = "INSERT INTO order_details(order_id, product_id, quantity, price) VALUES(?, ?, ?, ?)";
        String updateStockSQL = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmtOrder = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstmtOrderDetail = conn.prepareStatement(insertOrderDetailSQL);
             PreparedStatement pstmtUpdateStock = conn.prepareStatement(updateStockSQL)) {

            conn.setAutoCommit(false);

            // Insert order
            pstmtOrder.setString(1, order.getUsername());
            pstmtOrder.setString(2, order.getOrderDate().toString());
            pstmtOrder.setString(3, order.getStatus());
            pstmtOrder.setDouble(4, order.getTotal());
            int affectedRows = pstmtOrder.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            long orderId;
            try (ResultSet generatedKeys = pstmtOrder.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getLong(1);

                    for (OrderDetail detail : orderDetails) {
                        // Insert order detail
                        pstmtOrderDetail.setLong(1, orderId);
                        pstmtOrderDetail.setLong(2, detail.getProductId());
                        pstmtOrderDetail.setInt(3, detail.getQuantity());
                        pstmtOrderDetail.setDouble(4, detail.getPrice());
                        pstmtOrderDetail.addBatch();

                        // Update product stock
                        pstmtUpdateStock.setInt(1, detail.getQuantity());
                        pstmtUpdateStock.setLong(2, detail.getProductId());
                        pstmtUpdateStock.addBatch();
                    }

                    pstmtOrderDetail.executeBatch();
                    pstmtUpdateStock.executeBatch();
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            conn.commit();

            // Schedule task to update order status after 3 minutes
            scheduleStatusUpdate(orderId, "Completed");

        } catch (Exception e) {
            log.error("Error creating order", e);
            return false;
        }

        return true;
    }

    private void scheduleStatusUpdate(Long orderId, String status) {
        scheduler.schedule(() -> {
            try {
                log.info("Updating order status to {} for orderId {}", status, orderId);
                updateOrderStatus(orderId, status);
            } catch (SQLException e) {
                log.error("Failed to update order status for orderId {}", orderId, e);
            }
        }, 3, TimeUnit.MINUTES); // Update after 3 minutes
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
                    order.setTotal(rs.getDouble("total"));
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
                    order.setTotal(rs.getDouble("total"));
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
                    detail.setProductName(productService.getProductById(Math.toIntExact(detail.getProductId())).getName());
                    orderDetails.add(detail);
                }
            }
        }

        return orderDetails;
    }
}
