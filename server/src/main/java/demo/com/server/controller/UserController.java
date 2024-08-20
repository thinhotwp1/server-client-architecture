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

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) throws SQLException {
        return userService.getUserById(id);
    }

    @PostMapping("/register")
    public boolean registerUser(@RequestBody UserRegisterRequest request) throws SQLException {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public User loginUser(@RequestBody UserLoginRequest request) throws SQLException {
        return userService.loginUser(request.getUsername(), request.getPassword());
    }

    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody UserDeleteRequest request) throws SQLException {
        return userService.deleteUser(request.getId());
    }
}
