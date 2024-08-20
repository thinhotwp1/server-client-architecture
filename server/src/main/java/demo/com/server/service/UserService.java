package demo.com.server.service;

import demo.com.server.config.SQLiteConnection;
import demo.com.server.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class UserService {

    @PostConstruct
    public void init() {
        try {
            createTableIfNotExists();  // Tạo bảng khi khởi động
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Phương thức tạo bảng users
    public void createTableIfNotExists() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT NOT NULL, " +
                "username TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role INTEGER NOT NULL" +
                ");";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    // Lấy tất cả người dùng
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                users.add(user);
            }
        }
        return users;
    }

    // Lấy người dùng theo ID
    public User getUserById(long id) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getLong("id"));
                    user.setEmail(rs.getString("email"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getInt("role"));
                }
            }
        }
        return user;
    }

    // Đăng ký người dùng
    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users(email, username, password, role) VALUES(?, ?, ?, ?)";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setInt(4, user.getRole());
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error registering user", e);
            return false;
        }
        return true;
    }

    // Đăng nhập người dùng
    public User loginUser(String username, String password) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getLong("id"));
                    user.setEmail(rs.getString("email"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getInt("role"));
                }
            }
        }
        return user;
    }

    // Xóa người dùng
    public boolean deleteUser(long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error deleting user", e);
            return false;
        }
        return true;
    }
}
