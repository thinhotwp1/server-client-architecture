package demo.com.server.entity;

import lombok.Data;

@Data
public class OrderDetail {
    private Long id;
    private Long orderId;  // Khóa ngoại đến bảng Orders
    private Long productId;  // Khóa ngoại đến bảng Products
    private int quantity;
    private double price;  // Giá tại thời điểm đặt hàng
}