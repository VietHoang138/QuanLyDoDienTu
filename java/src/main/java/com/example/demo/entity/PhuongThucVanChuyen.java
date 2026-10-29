package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "PHUONG_THUC_VAN_CHUYEN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhuongThucVanChuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdPtvc")
    private Integer idPtvc;
    
    @Column(name = "TenPtvc", nullable = false)
    private String tenPtvc;
    
    @Column(name = "PhiCoBan")
    private BigDecimal phiCoBan;
    
    @Column(name = "TrangThai")
    private Boolean trangThai = true;
} 