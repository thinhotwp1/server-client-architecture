package demo.com.server.entity;

import lombok.Data;

@Data
public class Cart {
    private Long id;
    private Long userId;  // Khóa ngoại đến bảng Users
    private Long productId;  // Khóa ngoại đến bảng Products
    private int quantity;
}
