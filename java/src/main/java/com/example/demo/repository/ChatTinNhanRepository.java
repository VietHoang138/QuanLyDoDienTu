package com.example.demo.repository;

import com.example.demo.entity.ChatTinNhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChatTinNhanRepository extends JpaRepository<ChatTinNhan, Integer> {

    List<ChatTinNhan> findByChatSession_IdSessionOrderByNgayGuiAsc(Integer idSession);

    long countByChatSession_IdSessionAndNguoiGuiAndDaDocFalse(Integer idSession, String nguoiGui);

    @Modifying
    @Transactional
    @Query("UPDATE ChatTinNhan t SET t.daDoc = true "
            + "WHERE t.chatSession.idSession = :idSession AND t.nguoiGui = :nguoiGui AND t.daDoc = false")
    int markAsRead(@Param("idSession") Integer idSession, @Param("nguoiGui") String nguoiGui);
}
