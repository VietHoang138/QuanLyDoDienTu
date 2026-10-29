package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TAI_KHOAN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"khachHang", "hibernateLazyInitializer", "handler"})
public class TaiKhoan {
    @Id
    @Column(name = "TenDangNhap")
    private String tenDangNhap;

    @Column(name = "MatKhau", nullable = false)
    private String matKhau;

    @Column(name = "IdKH")
    private Integer idKH;
    
    @Column(name = "VaiTro")
    private String vaiTro;
    
    @ManyToOne
    @JoinColumn(name = "IdKH", insertable = false, updatable = false)
    private KhachHang khachHang;
} 