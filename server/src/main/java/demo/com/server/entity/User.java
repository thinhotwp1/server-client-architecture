package demo.com.server.entity;

import lombok.Data;

@Data
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String email;
    private int role;  // 0: Admin, 1: User
}