package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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