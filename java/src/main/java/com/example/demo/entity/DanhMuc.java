package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DANH_MUC")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhMuc {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDanhMuc")
    private Integer idDanhMuc;
    
    @Column(name = "TenDanhMuc", nullable = false, unique = true, length = 100)
    private String tenDanhMuc;
    
    @Column(name = "TrangThai")
    private Boolean trangThai = true;
} 