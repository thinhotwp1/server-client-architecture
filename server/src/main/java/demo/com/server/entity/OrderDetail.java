package demo.com.server.entity;

import lombok.Data;

@Data
public class OrderDetail {
    private Long orderDetailId;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private Double price;
}