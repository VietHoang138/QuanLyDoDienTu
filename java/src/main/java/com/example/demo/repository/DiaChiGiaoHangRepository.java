package com.example.demo.repository;

import com.example.demo.entity.DiaChiGiaoHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaChiGiaoHangRepository extends JpaRepository<DiaChiGiaoHang, Integer> {
    List<DiaChiGiaoHang> findByKhachHang_IdKH(Integer idKH);
} 