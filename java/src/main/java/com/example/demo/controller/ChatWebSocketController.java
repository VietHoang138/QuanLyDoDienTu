package com.example.demo.controller;

import com.example.demo.dto.ChatRequestDTO;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * STOMP: client gửi tới /app/chat
 * Server broadcast tới /topic/chat/{sessionId} và /topic/chat/admin
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    public void handleChat(@Payload ChatRequestDTO request) {
        if (request == null) {
            return;
        }

        String type = request.getType() == null ? "MESSAGE" : request.getType().trim().toUpperCase();
        try {
            switch (type) {
                case "START_SESSION" -> chatService.startOrResumeSession(request);
                case "JOIN" -> chatService.joinSession(request.getSessionId(), request.getAdminTenDangNhap());
                case "CLOSE" -> {
                    String nguoiDong = request.getTenNguoiGui() != null
                            ? request.getTenNguoiGui()
                            : request.getAdminTenDangNhap();
                    chatService.closeSession(request.getSessionId(), nguoiDong);
                }
                default -> chatService.sendMessage(request);
            }
        } catch (Exception e) {
            System.err.println("❌ Chat WS lỗi: " + e.getMessage());
        }
    }
}
