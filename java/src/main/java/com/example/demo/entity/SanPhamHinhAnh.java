package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SAN_PHAM_HINHANH")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SanPhamHinhAnh {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdAnh")
    private Integer idAnh;
    
    @ManyToOne
    @JoinColumn(name = "IdSP", nullable = false)
    @JsonIgnoreProperties({"hinhAnhList", "danhMucCha", "danhMucCon", "thuongHieu",
                            "mauSac", "dungLuong", "hibernateLazyInitializer", "handler"})
    private SanPham sanPham;
    
    @Column(name = "LienKet", nullable = false, length = 255)
    private String hinhAnh;
    
    @Column(name = "MoTa", length = 255)
    private String moTa;
}