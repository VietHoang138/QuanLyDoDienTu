package com.example.demo.repository;

import com.example.demo.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer> {

    List<ChatSession> findAllByOrderByNgayCapNhatDesc();

    List<ChatSession> findByTrangThaiOrderByNgayCapNhatDesc(String trangThai);

    List<ChatSession> findByIdKHOrderByNgayCapNhatDesc(Integer idKH);

    Optional<ChatSession> findFirstByIdKHAndTrangThaiInOrderByNgayTaoDesc(
            Integer idKH, Collection<String> trangThai);
}
