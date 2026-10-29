package com.example.demo.service;

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

    /**
     * Gửi thông báo khi có sản phẩm mới được thêm
     */
    public void notifyProductAdded(SanPhamListDTO product) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "PRODUCT_ADDED");
            message.put("data", product);
            message.put("timestamp", System.currentTimeMillis());
            
            // Gửi đến tất cả client đang kết nối
            messagingTemplate.convertAndSend("/topic/products", message);
            
            System.out.println("📡 WebSocket: Đã gửi thông báo sản phẩm mới - " + product.getTenSanPham());
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi WebSocket notification: " + e.getMessage());
        }
    }

    /**
     * Gửi thông báo khi sản phẩm được cập nhật
     */
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

    /**
     * Gửi thông báo khi sản phẩm bị xóa
     */
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

    /**
     * Gửi thông báo reload toàn bộ danh sách sản phẩm
     */
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

    /**
     * Gửi thông báo tùy chỉnh
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