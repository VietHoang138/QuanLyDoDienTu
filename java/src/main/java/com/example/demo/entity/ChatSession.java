package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "CHAT_SESSION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdSession")
    private Integer idSession;

    @Column(name = "IdKH")
    private Integer idKH;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdKH", insertable = false, updatable = false)
    @JsonIgnoreProperties({"taiKhoans", "matKhau", "hibernateLazyInitializer", "handler"})
    private KhachHang khachHang;

    @Column(name = "TenKhach")
    private String tenKhach;

    @Column(name = "AdminTenDangNhap")
    private String adminTenDangNhap;

    /** WAITING | OPEN | CLOSED */
    @Column(name = "TrangThai", nullable = false)
    private String trangThai = "WAITING";

    @Column(name = "NgayTao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao = new Date();

    @Column(name = "NgayCapNhat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayCapNhat = new Date();

    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ChatTinNhan> tinNhans = new ArrayList<>();
}
