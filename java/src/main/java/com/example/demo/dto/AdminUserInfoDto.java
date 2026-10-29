package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserInfoDto {
    private Integer idKH;
    private String hoTen;
    private String email;
    private String sdt;
    private String diaChi;
    private String matKhau; // Hiển thị mật khẩu cho admin
    private Integer diemTichLuy;
    private BigDecimal tongChiTieu;
    private Boolean trangThai;
    private Date ngayDangKy;
} 