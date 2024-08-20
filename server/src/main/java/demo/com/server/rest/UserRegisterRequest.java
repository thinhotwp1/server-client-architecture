package demo.com.server.rest;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String email;
    private String username;
    private String password;
    private int role;
}