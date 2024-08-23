package demo.com.server.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order {
    private Long orderId;
    private String username; // Username
    private LocalDateTime orderDate;
    private String status; // "Pending" or "Completed"
    private Double total;
}