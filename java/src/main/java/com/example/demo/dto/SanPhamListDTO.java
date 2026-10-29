package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamListDTO {
    private Integer idSp;
    private String tenSanPham;
    private String moTaNgan;
    private BigDecimal giaBan;
    private Integer soLuong;
    private Integer danhMucConId;
    private String tenDanhMuc;
    private List<String> hinhAnhUrls;
    private Boolean trangThai;
    
    @Override
    public String toString() {
        return "SanPhamListDTO{" +
                "idSp=" + idSp +
                ", tenSanPham='" + tenSanPham + '\'' +
                ", moTaNgan='" + moTaNgan + '\'' +
                ", giaBan=" + giaBan +
                ", soLuong=" + soLuong +
                ", danhMucConId=" + danhMucConId +
                ", tenDanhMuc='" + tenDanhMuc + '\'' +
                ", trangThai=" + trangThai +
                ", hinhAnhUrls=" + (hinhAnhUrls != null ? hinhAnhUrls.size() + " images" : "null") +
                '}';
    }
} 