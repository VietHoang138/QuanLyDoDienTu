package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "KHACH_HANG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"taiKhoans", "hibernateLazyInitializer", "handler"})
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdKH")
    private Integer idKH;

    @Column(name = "HoTen", nullable = false)
    private String hoTen;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "MatKhau", nullable = false)
    private String matKhau;

    @Column(name = "Sdt", unique = true)
    private String sdt;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "DiemTichLuy")
    private Integer diemTichLuy = 0;

    @Column(name = "TongChiTieu")
    private BigDecimal tongChiTieu = BigDecimal.ZERO;

    @Column(name = "TrangThai")
    private Boolean trangThai = true;

    @Column(name = "NgayDangKy")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayDangKy = new Date();
    
    @OneToMany(mappedBy = "khachHang")
    private List<TaiKhoan> taiKhoans;
} 