package com.example.demo.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wishlist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {
    
    @EmbeddedId
    private WishlistId id;
    
    @ManyToOne
    @MapsId("khachHangId")
    @JoinColumn(name = "khach_hang_id")
    private KhachHang khachHang;
    
    @ManyToOne
    @MapsId("sanPhamId")
    @JoinColumn(name = "san_pham_id")
    private SanPham sanPham;
} 