package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SAN_PHAM_DANHMUCCHA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDanhMucCha {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDanhMucCha")
    private Integer idDanhMucCha;
    
    @Column(name = "TenDanhMucCha", nullable = false)
    private String tenDanhMucCha;
} 