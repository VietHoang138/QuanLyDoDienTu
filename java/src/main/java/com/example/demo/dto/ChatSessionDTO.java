package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionDTO {

    private Integer idSession;
    private Integer idKH;
    private String tenKhach;
    private String adminTenDangNhap;
    private String trangThai;
    private Date ngayTao;
    private Date ngayCapNhat;
    private long soTinChuaDoc;
    private String tinNhanCuoi;
    private List<ChatMessageDTO> tinNhans;
}
