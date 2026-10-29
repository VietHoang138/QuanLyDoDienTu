package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamChiTietDTO {
    private Integer idSp;
    private String tenSanPham;
    private String moTaNgan;
    private BigDecimal giaBan;
    private Integer soLuong;
    private String tenDanhMuc;
    private String tenMau;
    private String dungLuong;
    private List<String> hinhAnhUrls;
} 