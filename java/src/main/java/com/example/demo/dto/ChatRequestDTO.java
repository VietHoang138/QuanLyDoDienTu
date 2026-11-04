package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload gửi lên STOMP destination /app/chat
 * hoặc REST POST /api/chat/...
 *
 * type: START_SESSION | MESSAGE | JOIN | CLOSE
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {

    private String type;
    private Integer sessionId;
    private Integer idKH;
    private String tenKhach;
    private String tenNguoiGui;
    /** CUSTOMER | ADMIN */
    private String nguoiGui;
    private String noiDung;
    private String adminTenDangNhap;
}
