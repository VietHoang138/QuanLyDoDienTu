package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DIA_CHI_GIAO_HANG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"khachHang", "hibernateLazyInitializer", "handler"})
public class DiaChiGiaoHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDiaChi")
    private Integer idDiaChi;
    
    @ManyToOne
    @JoinColumn(name = "KhachHangId")
    private KhachHang khachHang;
    
    @Column(name = "HoTenNguoiNhan")
    private String hoTenNguoiNhan;
    
    @Column(name = "SoDienThoai")
    private String soDienThoai;
    
    @Column(name = "DiaChiChiTiet")
    private String diaChiChiTiet;
} 