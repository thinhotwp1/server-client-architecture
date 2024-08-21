package demo.com.server.service;

import demo.com.server.config.SQLiteConnection;
import demo.com.server.config.UserCurrent;
import demo.com.server.entity.User;
import demo.com.server.rest.UserRegisterRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    private void init() throws SQLException {
        this.createTableIfNotExists();
    }

    public void createTableIfNotExists() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY NOT NULL, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role INTEGER NOT NULL" +
                ");";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getInt("role"));
                users.add(user);
            }
        }
        return users;
    }

    public User getUserByUsername(String username) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(rs.getString("password"));
                    user.setRole(rs.getInt("role"));
                }
            }
        }
        return user;
    }

    public boolean registerUser(UserRegisterRequest user) throws SQLException {
        User userExist = getUserByUsername(user.getUsername());
        if (userExist != null) {
            return false;
        }
        String sql = "INSERT INTO users(email, username, password, role) VALUES(?, ?, ?, ?)";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, passwordEncoder.encode(user.getPassword()));
            pstmt.setInt(4, user.getRole());
            pstmt.executeUpdate();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public User loginUser(String username, String password) throws SQLException {
        User user = getUserByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            UserCurrent.setCurrentUser(user);
            return user;
        }
        throw new RuntimeException("Invalid username or password");
    }

    public boolean deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
