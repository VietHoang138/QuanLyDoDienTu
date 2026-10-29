package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "MA_KHUYEN_MAI")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaKhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKM")
    private Integer maKM;
    
    @Column(name = "MoTa")
    private String moTa;
    
    @Column(name = "GiaTri", precision = 5, scale = 2)
    private BigDecimal giaTri;
    
    @Column(name = "NgayBatDau")
    @Temporal(TemporalType.DATE)
    private Date ngayBatDau;
    
    @Column(name = "NgayKetThuc")
    @Temporal(TemporalType.DATE)
    private Date ngayKetThuc;
    
    @Column(name = "SoLuong")
    private Integer soLuong;
    
    @Column(name = "TrangThai")
    private Boolean trangThai = true;
} 