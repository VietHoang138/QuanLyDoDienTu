package com.example.demo.controller;

import com.example.demo.entity.DanhMuc;
import com.example.demo.service.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/danhmuc")
@CrossOrigin("*")
public class DanhMucController {

    @Autowired
    private DanhMucService danhMucService;

    @GetMapping
    public ResponseEntity<List<DanhMuc>> getAllDanhMuc() {
        List<DanhMuc> danhMucs = danhMucService.getAllDanhMuc();
        return new ResponseEntity<>(danhMucs, HttpStatus.OK);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<DanhMuc>> getAllActiveDanhMuc() {
        List<DanhMuc> danhMucs = danhMucService.getAllActiveDanhMuc();
        return new ResponseEntity<>(danhMucs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DanhMuc> getDanhMucById(@PathVariable Integer id) {
        return danhMucService.getDanhMucById(id)
                .map(danhMuc -> new ResponseEntity<>(danhMuc, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}