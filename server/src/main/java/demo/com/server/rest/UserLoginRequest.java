package demo.com.server.rest;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
}