package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SANPHAM_MAUSAC")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamMauSac {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdMau")
    private Integer idMau;
    
    @Column(name = "TenMau", nullable = false)
    private String tenMau;
} 