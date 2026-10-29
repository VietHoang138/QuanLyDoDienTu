package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TRANG_THAI_DON_HANG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrangThaiDonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdTtdh")
    private Integer idTtdh;
    
    @Column(name = "TenTrangThai", nullable = false)
    private String tenTrangThai;
} 