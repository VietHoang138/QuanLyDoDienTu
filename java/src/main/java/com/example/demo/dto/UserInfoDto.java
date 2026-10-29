package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private Integer idKH;
    private String hoTen;
    private String email;
    private String sdt;
    private String diaChi;
    private Integer diemTichLuy;
    private Double tongChiTieu;
    private String vaiTro;
    private String matKhau;
} 