package com.example.demo.controller;

import com.example.demo.entity.SanPhamMauSac;
import com.example.demo.repository.SanPhamMauSacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mausac")
@CrossOrigin("*")
public class MauSacController {

    @Autowired
    private SanPhamMauSacRepository mauSacRepository;

    @GetMapping
    public ResponseEntity<List<SanPhamMauSac>> getAllMauSac() {
        List<SanPhamMauSac> mauSacs = mauSacRepository.findAll();
        return ResponseEntity.ok(mauSacs);
    }
} 