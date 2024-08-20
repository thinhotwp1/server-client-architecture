package demo.com.server.entity;

import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private Double price;
    private int stockQuantity;
}