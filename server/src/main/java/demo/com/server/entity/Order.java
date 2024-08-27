package demo.com.server.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order {
    private Long orderId;
    private String username;
    private LocalDateTime orderDate;
    private String status; // Pending, Shipping, Completed
    private Double total;
}