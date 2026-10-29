package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDTO {
    private String tenSanPham;
    private String moTaNgan;
    private BigDecimal giaBan;
    private Integer danhMucConId;
    private Integer soLuong;
    private Integer dungLuongId;
    private Integer mauSacId;
    private List<String> hinhAnhUrls; // Danh sách URL hình ảnh
} 