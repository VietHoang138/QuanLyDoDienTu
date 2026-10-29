package com.example.demo.repository;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, CartId> {
    List<Cart> findByKhachHang_IdKH(Integer idKH);
    
     // Custom query với JOIN để lấy thông tin đầy đủ
    @Query(value = "SELECT sp.IdSP as idSp, sp.TenSanPham as tenSanPham, " +
            "spha.HinhAnh as hinhAnh, c.SoLuong as soLuong, " +
            "sp.GiaBan as giaBan, c.SoLuong * sp.GiaBan as thanhTien " +
            "FROM CART c " +
            "INNER JOIN SAN_PHAM sp ON c.IdSP = sp.IdSP " +
            "INNER JOIN SAN_PHAM_HINHANH spha ON sp.IdSP = spha.IdSP " +
            "WHERE c.IdKH = :idKH", nativeQuery = true)
    List<Object[]> findCartItemsByIdKH(@Param("idKH") Integer idKH);
} 