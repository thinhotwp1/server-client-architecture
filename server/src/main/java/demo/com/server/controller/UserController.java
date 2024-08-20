package demo.com.server.controller;

import demo.com.server.entity.User;
import demo.com.server.rest.UserDeleteRequest;
import demo.com.server.rest.UserLoginRequest;
import demo.com.server.rest.UserRegisterRequest;
import demo.com.server.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() throws SQLException {
        return userService.getAllUsers();
    }

    @GetMapping("/{userName}")
    public User getUserByUserName(@PathVariable String userName) throws SQLException {
        return userService.getUserByUsername(userName);
    }

    @PostMapping("/register")
    public Object registerUser(@RequestBody UserRegisterRequest request) throws SQLException {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public boolean loginUser(@RequestBody UserLoginRequest request) throws SQLException {
        return userService.loginUser(request.getUsername(), request.getPassword());
    }

    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody UserDeleteRequest request) throws SQLException {
        return userService.deleteUser(request.getUserName());
    }
}
