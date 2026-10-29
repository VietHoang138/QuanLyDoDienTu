package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDonHang")
    private Integer idDonHang;

    @ManyToOne
    @JoinColumn(name = "KhachHangId")
    @JsonIgnoreProperties({"taiKhoans", "matKhau", "hibernateLazyInitializer", "handler"})
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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TrangThaiDonHang trangThai;

    @ManyToOne
    @JoinColumn(name = "PtttId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PhuongThucThanhToan phuongThucThanhToan;

    @ManyToOne
    @JoinColumn(name = "PtvcId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PhuongThucVanChuyen phuongThucVanChuyen;

    @ManyToOne
    @JoinColumn(name = "DiaChiGiaoHangId")
    @JsonIgnoreProperties({"khachHang", "hibernateLazyInitializer", "handler"})
    private DiaChiGiaoHang diaChiGiaoHang;

    @Column(name = "GhiChuKhachHang")
    private String ghiChuKhachHang;

    @Column(name = "ThongTinKhachHang")
    private String thongTinKhachHang;

    @ManyToOne
    @JoinColumn(name = "MaKM")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MaKhuyenMai maKhuyenMai;
}