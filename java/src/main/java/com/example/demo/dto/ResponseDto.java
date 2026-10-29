package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    
    public static <T> ResponseDto<T> success(String message, T data) {
        return new ResponseDto<>(true, message, data);
    }
    
    public static <T> ResponseDto<T> success(String message) {
        return new ResponseDto<>(true, message, null);
    }
    
    public static <T> ResponseDto<T> error(String message) {
        return new ResponseDto<>(false, message, null);
    }
} 