package com.example.demo.repository;

import com.example.demo.entity.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Integer> {
    List<ChiTietDonHang> findByDonHang_IdDonHang(Integer donHangId);

    @Query("SELECT c FROM ChiTietDonHang c LEFT JOIN FETCH c.sanPham LEFT JOIN FETCH c.donHang WHERE c.donHang IS NULL OR c.donHang.trangThai IS NULL OR c.donHang.trangThai.idTtdh != 5 ORDER BY c.idChiTietDonHang DESC")
    List<ChiTietDonHang> findAllValidSoldItems();
}

