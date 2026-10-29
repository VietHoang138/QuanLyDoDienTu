package com.example.demo.repository;

import com.example.demo.entity.TaiKhoanQuyen;
import com.example.demo.entity.TaiKhoanQuyen.TaiKhoanQuyenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaiKhoanQuyenRepository extends JpaRepository<TaiKhoanQuyen, TaiKhoanQuyenId> {
    List<TaiKhoanQuyen> findByTaiKhoan_TenDangNhap(String tenDangNhap);
    
    @Query("SELECT CASE WHEN COUNT(tkq) > 0 THEN true ELSE false END FROM TaiKhoanQuyen tkq " +
           "WHERE tkq.taiKhoan.tenDangNhap = ?1 AND tkq.quyen.tenQuyen = ?2")
    boolean kiemTraQuyen(String tenDangNhap, String tenQuyen);
} 