package com.example.demo.repository;

import com.example.demo.entity.Quyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuyenRepository extends JpaRepository<Quyen, Integer> {
    Optional<Quyen> findByTenQuyen(String tenQuyen);
} 