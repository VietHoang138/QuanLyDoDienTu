package com.example.demo.service;

import com.example.demo.repository.TaiKhoanQuyenRepository;
import com.example.demo.repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuyenService {
    
    @Autowired
    private TaiKhoanQuyenRepository taiKhoanQuyenRepository;
    
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    
    /**
     * Kiểm tra xem người dùng có quyền cụ thể hay không
     * 
     * @param tenDangNhap Tên đăng nhập cần kiểm tra
     * @param tenQuyen Tên quyền cần kiểm tra
     * @return true nếu có quyền, false nếu không có quyền
     */
    public boolean kiemTraQuyen(String tenDangNhap, String tenQuyen) {
        // Kiểm tra xem người dùng có phải là admin không
        if (taiKhoanRepository.findById(tenDangNhap)
                .map(tk -> "ADMIN".equals(tk.getVaiTro()))
                .orElse(false)) {
            return true; // Admin có tất cả các quyền
        }
        
        // Kiểm tra quyền cụ thể
        return taiKhoanQuyenRepository.kiemTraQuyen(tenDangNhap, tenQuyen);
    }
} 