package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "LICH_SU_DON_HANG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"donHang", "hibernateLazyInitializer", "handler"})
public class LichSuDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdLsDonHang")
    private Integer idLsDonHang;

    @ManyToOne
    @JoinColumn(name = "DonHangId")
    private DonHang donHang;

    @ManyToOne
    @JoinColumn(name = "TrangThaiId")
    private TrangThaiDonHang trangThai;

    @Column(name = "NgayTaoDon")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTaoDon = new Date();
}
