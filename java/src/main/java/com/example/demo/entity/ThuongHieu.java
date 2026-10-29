package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "THUONG_HIEU")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThuongHieu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdThuongHieu")
    private Integer idThuongHieu;
    
    @Column(name = "TenThuongHieu", nullable = false)
    private String tenThuongHieu;
    
    @Column(name = "QuocGia")
    private String quocGia;
} 