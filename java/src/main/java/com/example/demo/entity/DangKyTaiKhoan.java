package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "DANG_KY_TAI_KHOAN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DangKyTaiKhoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDangKy")
    private Integer idDangKy;

    @Column(name = "TenDangNhap", nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "MatKhau", nullable = false)
    private String matKhau;

    @Column(name = "Sdt")
    private String sdt;

    @Column(name = "MaXacThuc")
    private String maXacThuc;

    @Column(name = "NgayYeuCau")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayYeuCau = new Date();

    @Column(name = "TrangThai", nullable = false)
    private String trangThai = "Đang chờ";
    
    @ManyToOne
    @JoinColumn(name = "TenDangNhap", referencedColumnName = "TenDangNhap", insertable = false, updatable = false)
    private TaiKhoan taiKhoan;
} 