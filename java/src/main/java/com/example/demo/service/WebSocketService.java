package com.example.demo.service;

import com.example.demo.dto.ChatMessageDTO;
import com.example.demo.dto.DonHangNotificationDTO;
import com.example.demo.dto.SanPhamListDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // ═══════════════════════════════════════════════════════════
    //  SẢN PHẨM
    // ═══════════════════════════════════════════════════════════

    public void notifyProductAdded(SanPhamListDTO product) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "PRODUCT_ADDED");
            message.put("data", product);
            message.put("timestamp", System.currentTimeMillis());
            messagingTemplate.convertAndSend("/topic/products", message);
            System.out.println("📡 WebSocket: Đã gửi thông báo sản phẩm mới - " + product.getTenSanPham());
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi WebSocket notification: " + e.getMessage());
        }
    }

    public void notifyProductUpdated(SanPhamListDTO product) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "PRODUCT_UPDATED");
            message.put("data", product);
            message.put("timestamp", System.currentTimeMillis());
            messagingTemplate.convertAndSend("/topic/products", message);
            System.out.println("📡 WebSocket: Đã gửi thông báo cập nhật sản phẩm - " + product.getTenSanPham());
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi WebSocket notification: " + e.getMessage());
        }
    }

    public void notifyProductDeleted(Integer productId) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "PRODUCT_DELETED");
            message.put("productId", productId);
            message.put("timestamp", System.currentTimeMillis());
            messagingTemplate.convertAndSend("/topic/products", message);
            System.out.println("📡 WebSocket: Đã gửi thông báo xóa sản phẩm ID - " + productId);
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi WebSocket notification: " + e.getMessage());
        }
    }

    public void notifyProductsReload() {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "PRODUCTS_RELOAD");
            message.put("timestamp", System.currentTimeMillis());
            messagingTemplate.convertAndSend("/topic/products", message);
            System.out.println("📡 WebSocket: Đã gửi thông báo reload danh sách sản phẩm");
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi WebSocket notification: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  ĐƠN HÀNG — gửi đến /topic/orders (admin lắng nghe)
    //             gửi đến /topic/orders/{idKH} (khách hàng lắng nghe)
    // ═══════════════════════════════════════════════════════════

    /**
     * Broadcast đến admin khi có đơn hàng MỚI vừa được đặt.
     * Admin subscribe: /topic/orders
     */
    public void notifyNewOrder(DonHangNotificationDTO notification) {
        try {
            notification.setType("NEW_ORDER");
            notification.setTimestamp(System.currentTimeMillis());
            if (notification.getMessage() == null) {
                notification.setMessage("Đơn hàng mới #" + notification.getDonHangId()
                        + " từ " + notification.getTenKhachHang());
            }
            messagingTemplate.convertAndSend("/topic/orders", notification);
            System.out.println("📡 WebSocket [NEW_ORDER]: Đơn hàng #" + notification.getDonHangId());
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi WebSocket NEW_ORDER: " + e.getMessage());
        }
    }

    /**
     * Broadcast đến admin khi trạng thái đơn hàng được cập nhật.
     * Admin subscribe: /topic/orders
     * Khách hàng subscribe: /topic/orders/{idKH}
     */
    public void notifyOrderStatusUpdated(DonHangNotificationDTO notification) {
        try {
            notification.setType("STATUS_UPDATED");
            notification.setTimestamp(System.currentTimeMillis());
            if (notification.getMessage() == null) {
                notification.setMessage("Đơn hàng #" + notification.getDonHangId()
                        + " → " + notification.getTenTrangThai());
            }

            // Gửi cho admin
            messagingTemplate.convertAndSend("/topic/orders", notification);

            // Gửi riêng cho khách hàng (nếu có idKH)
            if (notification.getKhachHangId() != null) {
                messagingTemplate.convertAndSend(
                        "/topic/orders/" + notification.getKhachHangId(), notification);
            }

            System.out.println("📡 WebSocket [STATUS_UPDATED]: Đơn #"
                    + notification.getDonHangId() + " → " + notification.getTenTrangThai());
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi WebSocket STATUS_UPDATED: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  CHAT HỖ TRỢ — /topic/chat/{sessionId}  (khách + admin cùng phòng)
    //               /topic/chat/admin         (danh sách hội thoại)
    // ═══════════════════════════════════════════════════════════

    public void notifyChatMessage(ChatMessageDTO payload) {
        try {
            if (payload.getTimestamp() == 0) {
                payload.setTimestamp(System.currentTimeMillis());
            }
            messagingTemplate.convertAndSend("/topic/chat/" + payload.getSessionId(), payload);
            System.out.println("📡 WebSocket [CHAT]: session #" + payload.getSessionId()
                    + " type=" + payload.getType());
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi WebSocket CHAT: " + e.getMessage());
        }
    }

    public void notifyChatAdmin(ChatMessageDTO payload) {
        try {
            if (payload.getTimestamp() == 0) {
                payload.setTimestamp(System.currentTimeMillis());
            }
            messagingTemplate.convertAndSend("/topic/chat/admin", payload);
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi WebSocket CHAT admin: " + e.getMessage());
        }
    }

    /**
     * Gửi thông báo tùy chỉnh đến bất kỳ topic nào.
     */
    public void sendCustomMessage(String topic, Object message) {
        try {
            messagingTemplate.convertAndSend("/topic/" + topic, message);
            System.out.println("📡 WebSocket: Đã gửi thông báo tùy chỉnh đến " + topic);
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi WebSocket custom message: " + e.getMessage());
        }
    }
} 