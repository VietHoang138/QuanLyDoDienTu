package com.example.demo.repository;

import com.example.demo.entity.LichSuDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuDonHangRepository extends JpaRepository<LichSuDonHang, Integer> {
    List<LichSuDonHang> findByDonHang_IdDonHangOrderByNgayTaoDonAsc(Integer idDonHang);
}
