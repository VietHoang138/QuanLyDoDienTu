package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SAN_PHAM_HINHANH")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamHinhAnh {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdAnh")
    private Integer idAnh;
    
    @ManyToOne
    @JoinColumn(name = "IdSP", nullable = false)
    private SanPham sanPham;
    
    @Column(name = "LienKet", nullable = false, length = 255)
    private String hinhAnh;  // Lưu URL của hình ảnh
    
    @Column(name = "MoTa", length = 255)
    private String moTa;
} 