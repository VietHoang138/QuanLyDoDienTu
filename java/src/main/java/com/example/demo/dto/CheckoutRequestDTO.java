package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CheckoutRequestDTO {
    private Integer khachHangId;
    private BigDecimal tongTienSanPham;
    private String hoKhachHang;
    private String tenKhachHang;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String ghiChuDonHang;
} 