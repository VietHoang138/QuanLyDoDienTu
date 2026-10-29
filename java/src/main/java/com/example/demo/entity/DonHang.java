package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "DON_HANG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDonHang")
    private Integer idDonHang;

    @ManyToOne
    @JoinColumn(name = "KhachHangId")
    private KhachHang khachHang;

    @Column(name = "TongTienSanPham")
    private BigDecimal tongTienSanPham;

    @Column(name = "PhiVanChuyen")
    private BigDecimal phiVanChuyen;

    @Column(name = "TongTienGiamGia")
    private BigDecimal tongTienGiamGia;

    @Column(name = "TongThanhToan")
    private BigDecimal tongThanhToan;

    @Column(name = "NgayDat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayDat = new Date();

    @ManyToOne
    @JoinColumn(name = "TrangThaiId")
    private TrangThaiDonHang trangThai;

    @ManyToOne
    @JoinColumn(name = "PtttId")
    private PhuongThucThanhToan phuongThucThanhToan;

    @ManyToOne
    @JoinColumn(name = "PtvcId")
    private PhuongThucVanChuyen phuongThucVanChuyen;

    @ManyToOne
    @JoinColumn(name = "DiaChiGiaoHangId")
    private DiaChiGiaoHang diaChiGiaoHang;

    @Column(name = "GhiChuKhachHang")
    private String ghiChuKhachHang;

    @Column(name = "ThongTinKhachHang")
    private String thongTinKhachHang;

    @ManyToOne
    @JoinColumn(name = "MaKM")
    private MaKhuyenMai maKhuyenMai;
} 