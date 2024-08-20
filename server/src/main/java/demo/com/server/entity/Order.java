package demo.com.server.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Order {
    private Long id;
    private Long userId;  // Khóa ngoại đến bảng Users
    private Date orderDate;
    private String status;  // Trạng thái đơn hàng: "Pending", "Completed", etc.
}