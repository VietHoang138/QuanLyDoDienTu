package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "QUYEN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdQuyen")
    private Integer idQuyen;
    
    @Column(name = "TenQuyen", nullable = false, unique = true)
    private String tenQuyen;
    
    @Column(name = "MoTa")
    private String moTa;
} 