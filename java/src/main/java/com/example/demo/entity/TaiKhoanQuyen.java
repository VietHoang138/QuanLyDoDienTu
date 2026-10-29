package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TAI_KHOAN_QUYEN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanQuyen {
    @EmbeddedId
    private TaiKhoanQuyenId id;
    
    @ManyToOne
    @MapsId("tenDangNhap")
    @JoinColumn(name = "TenDangNhap")
    private TaiKhoan taiKhoan;
    
    @ManyToOne
    @MapsId("idQuyen")
    @JoinColumn(name = "IdQuyen")
    private Quyen quyen;
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaiKhoanQuyenId implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        
        @Column(name = "TenDangNhap")
        private String tenDangNhap;
        
        @Column(name = "IdQuyen")
        private Integer idQuyen;
    }
} 