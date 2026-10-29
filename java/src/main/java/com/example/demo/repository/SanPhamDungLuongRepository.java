package com.example.demo.repository;

import com.example.demo.entity.SanPhamDungLuong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanPhamDungLuongRepository extends JpaRepository<SanPhamDungLuong, Integer> {
} 