package com.example.demo.service;

import com.example.demo.entity.DanhMuc;
import com.example.demo.repository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DanhMucService {
    
    @Autowired
    private DanhMucRepository danhMucRepository;
    
    public List<DanhMuc> getAllDanhMuc() {
        return danhMucRepository.findAll();
    }
    
    public List<DanhMuc> getAllActiveDanhMuc() {
        return danhMucRepository.findAllByTrangThaiIsTrue();
    }
    
    public Optional<DanhMuc> getDanhMucById(Integer id) {
        return danhMucRepository.findById(id);
    }
} 