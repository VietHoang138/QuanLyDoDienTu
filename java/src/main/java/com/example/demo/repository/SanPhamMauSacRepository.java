package com.example.demo.repository;

import com.example.demo.entity.SanPhamMauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanPhamMauSacRepository extends JpaRepository<SanPhamMauSac, Integer> {
} 