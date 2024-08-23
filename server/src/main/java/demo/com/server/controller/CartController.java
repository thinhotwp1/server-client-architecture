package demo.com.server.controller;

import demo.com.server.entity.Cart;
import demo.com.server.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userName}")
    public Cart getCartByUserName(@PathVariable String userName) throws SQLException {
        return cartService.getCartByUserName(userName);
    }

    @PostMapping("/add")
    public boolean addProductToCart(@RequestParam String userName, @RequestParam Long productId, @RequestParam int quantity) throws SQLException {
        return cartService.addProductToCart(userName, productId, quantity);
    }

    @PostMapping("/update")
    public boolean updateCart(@RequestParam String userName, @RequestParam Long productId, @RequestParam int quantity) throws SQLException {
        return cartService.updateCart(userName, productId, quantity);
    }

    @PostMapping("/remove")
    public boolean removeProductFromCart(@RequestParam String userName, @RequestParam Long productId) throws SQLException {
        return cartService.removeProductFromCart(userName, productId);
    }

    @PostMapping("/clear")
    public boolean clearCart(@RequestParam String userName) throws SQLException {
        return cartService.clearCart(userName);
    }
}
