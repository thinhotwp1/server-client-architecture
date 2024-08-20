package demo.com.server.entity;

import lombok.Data;

@Data
public class Customer {
    private Long id;
    private Long userId;  // Khóa ngoại đến bảng Users
    private String name;
    private String email;
}
