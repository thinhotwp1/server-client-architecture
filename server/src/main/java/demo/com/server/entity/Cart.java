package demo.com.server.entity;

import lombok.Data;

import java.util.List;

@Data
public class Cart {
    private String userName;
    private List<CartItem> cartItems;
}
