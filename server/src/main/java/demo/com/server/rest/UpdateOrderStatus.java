package demo.com.server.rest;

import lombok.Data;

@Data
public class UpdateOrderStatus {
    private Long orderId;
    private String status;
}
