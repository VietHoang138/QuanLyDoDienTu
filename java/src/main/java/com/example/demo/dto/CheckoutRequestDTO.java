package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

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

    // Danh sách sản phẩm từ frontend (dùng để trừ kho khi giỏ hàng lưu localStorage)
    private List<ChiTietDonHangDTO> chiTietDonHang;

    @Data
    public static class ChiTietDonHangDTO {
        private Integer sanPhamId;
        private Integer soLuong;
        private BigDecimal donGia;
        private BigDecimal thanhTien;
    }
}