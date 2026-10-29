package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "SAN_PHAM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdSP")
    private Integer idSp;

    @Column(name = "TenSanPham", nullable = false, length = 200)
    private String tenSanPham;

    @Column(name = "MoTaNgan", length = 255)
    private String moTaNgan;

    @Column(name = "GiaBan", precision = 18, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @ManyToOne
    @JoinColumn(name = "DanhMucChaId")
    private SanPhamDanhMucCha danhMucCha;

    @ManyToOne
    @JoinColumn(name = "DanhMucConId")
    private DanhMuc danhMucCon;

    @ManyToOne
    @JoinColumn(name = "ThuongHieuId")
    private ThuongHieu thuongHieu;

    @Column(name = "BaoHanh", length = 100)
    private String baoHanh;

    @Column(name = "CanNang", length = 50)
    private String canNang;

    @Column(name = "KichThuoc", length = 100)
    private String kichThuoc;

    @ManyToOne
    @JoinColumn(name = "MauSac")
    private SanPhamMauSac mauSac;

    @ManyToOne
    @JoinColumn(name = "DungLuong")
    private SanPhamDungLuong dungLuong;

    @Column(name = "TrangThai", nullable = false)
    private Boolean trangThai = true;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SanPhamHinhAnh> hinhAnhList;
} 