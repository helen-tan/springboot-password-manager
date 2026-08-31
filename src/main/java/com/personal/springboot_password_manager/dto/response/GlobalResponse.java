package com.personal.springboot_password_manager.dto.response;

import lombok.Data;

@Data
public class GlobalResponse<T> {
    private int statusCode;
    private String message;
    private T data;

    public GlobalResponse() {
    }

    public GlobalResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
}
