package demo.com.server.entity;

import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private String urlImage;
    private Double price;
    private int stockQuantity;
    private Category category;
}