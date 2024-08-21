package demo.com.server.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Cart {
    private Long id;
    private Long userId;
    private List<Map<Long,Integer>> productList; // Map<Id Product, Quantity>
}
