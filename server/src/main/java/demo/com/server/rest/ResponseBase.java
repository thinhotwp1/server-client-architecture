package demo.com.server.rest;

import lombok.Data;

@Data
public class ResponseBase {
    private int code;
    private String message;
}
