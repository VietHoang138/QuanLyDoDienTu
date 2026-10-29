package com.example.demo.repository;

import com.example.demo.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
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
} 