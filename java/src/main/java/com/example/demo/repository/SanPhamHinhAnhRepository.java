package com.example.demo.repository;

import com.example.demo.entity.SanPhamHinhAnh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SanPhamHinhAnhRepository extends JpaRepository<SanPhamHinhAnh, Integer> {
    @Query("SELECT h.hinhAnh FROM SanPhamHinhAnh h WHERE h.sanPham.idSp = :idSp")
    List<String> findHinhAnhUrlsByIdSp(@Param("idSp") Integer idSp);
} 