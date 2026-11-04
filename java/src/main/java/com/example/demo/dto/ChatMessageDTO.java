package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    /** SESSION_CREATED | JOINED | MESSAGE | CLOSED | SESSION_UPDATED */
    private String type;

    private Integer idTinNhan;
    private Integer sessionId;
    private Integer idKH;
    private String tenKhach;
    private String adminTenDangNhap;
    private String trangThai;

    /** CUSTOMER | ADMIN | SYSTEM */
    private String nguoiGui;
    private String tenNguoiGui;
    private String noiDung;
    private Date ngayGui;
    private Boolean daDoc;

    private long timestamp;
}
