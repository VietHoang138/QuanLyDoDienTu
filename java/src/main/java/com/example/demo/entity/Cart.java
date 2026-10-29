package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "CART")
@Data
public class Cart {
    @EmbeddedId
    private CartId id;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "NgayThem")
    private LocalDateTime ngayThem;

    @ManyToOne
    @MapsId("idKh")
    @JoinColumn(name = "IdKH")
    private KhachHang khachHang;

    @ManyToOne
    @MapsId("idSp")
    @JoinColumn(name = "IdSP")
    private SanPham sanPham;
} 