package demo.com.server.entity;

import lombok.Data;

import lombok.Data;

@Data
public class User {
    private long id;
    private String email;
    private String username;
    private String password;
    private int role;  // 0: admin, 1: user
}
