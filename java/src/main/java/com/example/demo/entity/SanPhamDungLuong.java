package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SANPHAM_DUNGLUONG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDungLuong {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDungLuong")
    private Integer idDungLuong;
    
    @Column(name = "DungLuong", nullable = false)
    private String dungLuong;
} 