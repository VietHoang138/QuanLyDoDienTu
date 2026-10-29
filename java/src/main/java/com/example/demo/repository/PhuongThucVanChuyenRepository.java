package com.example.demo.repository;

import com.example.demo.entity.PhuongThucVanChuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhuongThucVanChuyenRepository extends JpaRepository<PhuongThucVanChuyen, Integer> {
} 