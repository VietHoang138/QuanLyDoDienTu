package com.example.demo.controller;

import com.example.demo.dto.ResponseDto;
import com.example.demo.service.QuyenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quyen")
@CrossOrigin("*")
public class QuyenController {
    
    @Autowired
    private QuyenService quyenService;
    
    @GetMapping("/kiem-tra")
    public ResponseEntity<ResponseDto<Boolean>> kiemTraQuyen(
            @RequestParam("tenDangNhap") String tenDangNhap,
            @RequestParam("tenQuyen") String tenQuyen) {
        
        try {
            boolean coQuyen = quyenService.kiemTraQuyen(tenDangNhap, tenQuyen);
            
            if (coQuyen) {
                return ResponseEntity.ok(ResponseDto.success("Người dùng có quyền này", true));
            } else {
                return ResponseEntity.ok(ResponseDto.success("Người dùng không có quyền này", false));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Lỗi kiểm tra quyền: " + e.getMessage()));
        }
    }
} 