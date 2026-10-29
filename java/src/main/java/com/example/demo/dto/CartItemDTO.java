package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Integer idSp;
    private String tenSanPham;
    private String hinhAnh;
    private Integer soLuong;
    private BigDecimal giaBan;
    private BigDecimal thanhTien;
} 