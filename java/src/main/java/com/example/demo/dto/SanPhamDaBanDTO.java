package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDaBanDTO {
    private Integer idSp;
    private String tenSanPham;
    private String hinhAnh;
    private String tenDanhMuc;
    private BigDecimal giaBan;
    private Integer soLuongDaBan;
    private BigDecimal tongTienBan;
    private Integer soLuongTonKho;
    private Integer soDonHang;
    private Date ngayBanGanNhat;
    private List<DonHangItemDTO> danhSachDonHang;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonHangItemDTO {
        private Integer idDonHang;
        private String maDonHang;
        private String tenKhachHang;
        private String thongTinKhachHang;
        private Date ngayDat;
        private Integer soLuong;
        private BigDecimal donGia;
        private BigDecimal thanhTien;
        private Integer trangThaiId;
        private String tenTrangThai;
    }
}

