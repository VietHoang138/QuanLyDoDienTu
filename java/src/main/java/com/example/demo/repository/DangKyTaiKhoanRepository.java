package com.example.demo.repository;

import com.example.demo.entity.DangKyTaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DangKyTaiKhoanRepository extends JpaRepository<DangKyTaiKhoan, Integer> {
    Optional<DangKyTaiKhoan> findByTenDangNhap(String tenDangNhap);
    Optional<DangKyTaiKhoan> findByEmail(String email);
    Optional<DangKyTaiKhoan> findByMaXacThuc(String maXacThuc);
    boolean existsByTenDangNhap(String tenDangNhap);
    boolean existsByEmail(String email);
} 