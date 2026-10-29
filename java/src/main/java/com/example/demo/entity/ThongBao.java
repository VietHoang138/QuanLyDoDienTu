package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "THONG_BAO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdThongBao")
    private Integer idThongBao;

    @Column(name = "TieuDe", length = 255)
    private String tieuDe;

    @Column(name = "NoiDung", length = 1000)
    private String noiDung;

    @Column(name = "NgayGui")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayGui = new Date();

    // Null = thông báo toàn hệ thống (gửi cho tất cả)
    @Column(name = "IdKH")
    private Integer idKH;

    @Column(name = "DaDoc")
    private Boolean daDoc = false;
}
