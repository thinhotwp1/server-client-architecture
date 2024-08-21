package demo.com.server.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Order {
    private Long orderId;
    private String username; // Username
    private LocalDateTime orderDate;
    private String status; // "Pending" or "Completed"
}