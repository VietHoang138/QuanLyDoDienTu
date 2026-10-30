package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "CHAT_TIN_NHAN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"chatSession", "hibernateLazyInitializer", "handler"})
public class ChatTinNhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdTinNhan")
    private Integer idTinNhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdSession", nullable = false)
    private ChatSession chatSession;

    /** CUSTOMER | ADMIN | SYSTEM */
    @Column(name = "NguoiGui", nullable = false)
    private String nguoiGui;

    @Column(name = "TenNguoiGui")
    private String tenNguoiGui;

    @Column(name = "NoiDung", nullable = false, length = 2000)
    private String noiDung;

    @Column(name = "NgayGui")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayGui = new Date();

    @Column(name = "DaDoc")
    private Boolean daDoc = false;
}
