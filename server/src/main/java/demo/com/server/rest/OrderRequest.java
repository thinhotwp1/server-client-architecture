package demo.com.server.rest;

import demo.com.server.entity.OrderDetail;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String username;
    private List<OrderDetail> orderDetails;
}
