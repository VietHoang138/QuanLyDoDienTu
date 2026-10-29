package com.example.demo.repository;

import com.example.demo.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {

    // Lấy thông báo của 1 khách hàng (mới nhất trước)
    List<ThongBao> findByIdKHOrderByNgayGuiDesc(Integer idKH);

    // Đếm thông báo chưa đọc của 1 khách hàng
    long countByIdKHAndDaDocFalse(Integer idKH);

    // Đánh dấu tất cả đã đọc cho 1 khách hàng
    @Modifying
    @Transactional
    @Query("UPDATE ThongBao t SET t.daDoc = true WHERE t.idKH = :idKH AND t.daDoc = false")
    int markAllAsRead(@Param("idKH") Integer idKH);
}
