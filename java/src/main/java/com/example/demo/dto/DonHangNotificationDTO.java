package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO dùng để gửi qua WebSocket khi có sự kiện liên quan đến đơn hàng.
 * type: NEW_ORDER | STATUS_UPDATED | ORDER_CANCELLED
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonHangNotificationDTO {

    private String type;           // loại sự kiện: NEW_ORDER, STATUS_UPDATED, ORDER_CANCELLED

    // Thông tin đơn hàng
    private Integer donHangId;
    private Integer khachHangId;
    private String  tenKhachHang;
    private String  thongTinKhachHang;
    private BigDecimal tongThanhToan;
    private Date    ngayDat;

    // Thông tin trạng thái
    private Integer trangThaiId;
    private String  tenTrangThai;
    private String  trangThaiCu;   // tên trạng thái trước khi đổi (dùng cho STATUS_UPDATED)

    private long    timestamp;     // epoch ms
    private String  message;       // mô tả sự kiện
}
