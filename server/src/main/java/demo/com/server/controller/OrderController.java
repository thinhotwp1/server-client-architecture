package demo.com.server.controller;

import demo.com.server.entity.Order;
import demo.com.server.entity.OrderDetail;
import demo.com.server.rest.OrderRequest;
import demo.com.server.rest.UpdateOrderStatus;
import demo.com.server.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public boolean createOrder(@RequestBody OrderRequest orderRequest) throws SQLException {
        Order order = new Order();
        order.setUsername(orderRequest.getUsername());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Pending");

        List<OrderDetail> orderDetails = orderRequest.getOrderDetails();
        return orderService.createOrder(order, orderDetails);
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable Long orderId) throws SQLException {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/user/{username}")
    public List<Order> getOrdersByUsername(@PathVariable String username) throws SQLException {
        return orderService.getOrdersByUsername(username);
    }

    @PostMapping("/updateStatus")
    public boolean updateOrderStatus(@RequestBody UpdateOrderStatus request) throws SQLException {
        return orderService.updateOrderStatus(request.getOrderId(), request.getStatus());
    }

    @GetMapping("/details/{orderId}")
    public List<OrderDetail> getOrderDetailsByOrderId(@PathVariable Long orderId) throws SQLException {
        return orderService.getOrderDetailsByOrderId(orderId);
    }
}
