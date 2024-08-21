package demo.com.server.service;

import demo.com.server.config.SQLiteConnection;
import demo.com.server.entity.Category;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class CategoryService {

    @PostConstruct
    public void init() {
        try {
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Phương thức tạo bảng
    public void createTableIfNotExists() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS categories (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    category_name TEXT NOT NULL\n" +
                ");";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    // Lấy tất cả categories
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getLong("id"));
                category.setCategoryName(rs.getString("category_name"));
                categories.add(category);
            }
        }
        return categories;
    }

    // Lấy category theo id
    public Category getCategoryById(Long id) throws SQLException {
        Category category = null;
        String sql = "SELECT * FROM categories WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    category = new Category();
                    category.setId(rs.getLong("id"));
                    category.setCategoryName(rs.getString("category_name"));
                }
            }
        }
        return category;
    }

    // Tạo mới category
    public boolean createCategory(Category category) throws SQLException {
        String sql = "INSERT INTO categories (category_name) VALUES (?)";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category.getCategoryName());
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error creating category", e);
            return false;
        }
        return true;
    }

    // Cập nhật category
    public boolean updateCategory(Category category) throws SQLException {
        String sql = "UPDATE categories SET category_name = ? WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getCategoryName());
            pstmt.setLong(2, category.getId());
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error updating category", e);
            return false;
        }
        return true;
    }

    // Xóa category
    public boolean deleteCategory(Long id) throws SQLException {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("Error deleting category", e);
            return false;
        }
        return true;
    }
}
