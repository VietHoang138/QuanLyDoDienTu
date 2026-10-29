package com.example.demo.repository;

import com.example.demo.entity.Wishlist;
import com.example.demo.entity.WishlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {
    
    /**
     * Tìm tất cả các mục yêu thích của một khách hàng
     * @param khachHangId ID của khách hàng
     * @return Danh sách các mục yêu thích
     */
    List<Wishlist> findByIdKhachHangId(Integer khachHangId);
    
} 