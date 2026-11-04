package com.example.demo.repository;

import com.example.demo.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
    
    @Query(value = "SELECT DISTINCT s.* FROM SAN_PHAM s " +
                  "INNER JOIN SAN_PHAM_HINHANH spha ON s.IdSP = spha.IdSP " +
                  "INNER JOIN DANH_MUC dm ON dm.IdDanhMuc = s.DanhMucConId " +
                  "WHERE s.TrangThai = 1 " +
                  "ORDER BY s.IdSP DESC", nativeQuery = true)
    List<SanPham> findAllWithImagesAndCategory();
    
    @Query(value = "SELECT DISTINCT s.* FROM SAN_PHAM s " +
                  "INNER JOIN SAN_PHAM_HINHANH spha ON s.IdSP = spha.IdSP " +
                  "INNER JOIN DANH_MUC dm ON dm.IdDanhMuc = s.DanhMucConId " +
                  "WHERE s.TrangThai = 1 " +
                  "ORDER BY s.IdSP DESC", nativeQuery = true)
    List<SanPham> findLatestProducts();
    
    @Query("SELECT s.idSp FROM SanPham s WHERE s.trangThai = true ORDER BY s.idSp DESC")
    List<Integer> findAllSanPhamIds();

    /**
     * Trừ tồn kho sau khi đặt hàng thành công.
     * Chỉ trừ nếu số lượng còn đủ (SoLuong >= :soLuong) để tránh âm kho.
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE SAN_PHAM SET SoLuong = SoLuong - :soLuong " +
                  "WHERE IdSP = :idSp AND SoLuong >= :soLuong", nativeQuery = true)
    int decreaseStock(@Param("idSp") Integer idSp, @Param("soLuong") Integer soLuong);
} 