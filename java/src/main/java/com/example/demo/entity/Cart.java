package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "CART")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @JsonIgnoreProperties({"taiKhoans", "matKhau", "hibernateLazyInitializer", "handler"})
    private KhachHang khachHang;

    @ManyToOne
    @MapsId("idSp")
    @JoinColumn(name = "IdSP")
    @JsonIgnoreProperties({"hinhAnhList", "danhMucCha", "danhMucCon", "thuongHieu",
                            "mauSac", "dungLuong", "hibernateLazyInitializer", "handler"})
    private SanPham sanPham;
} 