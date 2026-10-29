package com.example.demo.controller;

import com.example.demo.entity.SanPhamDungLuong;
import com.example.demo.repository.SanPhamDungLuongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dungluong")
@CrossOrigin("*")
public class DungLuongController {

    @Autowired
    private SanPhamDungLuongRepository dungLuongRepository;

    @GetMapping
    public ResponseEntity<List<SanPhamDungLuong>> getAllDungLuong() {
        List<SanPhamDungLuong> dungLuongs = dungLuongRepository.findAll();
        return ResponseEntity.ok(dungLuongs);
    }
} 