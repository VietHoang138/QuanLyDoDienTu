package com.example.demo.controller;

import com.example.demo.dto.CheckoutRequestDTO;
import com.example.demo.dto.CheckoutResponseDTO;
import com.example.demo.entity.DonHang;
import com.example.demo.entity.LichSuDonHang;
import com.example.demo.entity.ThongBao;
import com.example.demo.repository.ThongBaoRepository;
import com.example.demo.service.DonHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final DonHangService      donHangService;
    private final ThongBaoRepository  thongBaoRepository;

    // ── ĐẶT HÀNG ─────────────────────────────────────────────────────────────
    @PostMapping("/place-order")
    public ResponseEntity<CheckoutResponseDTO> placeOrder(@RequestBody CheckoutRequestDTO request) {
        CheckoutResponseDTO response = donHangService.createOrder(request);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    // ── LẤY ĐƠN HÀNG ─────────────────────────────────────────────────────────
    @GetMapping("/orders/{khachHangId}")
    public ResponseEntity<List<DonHang>> getOrdersByCustomerId(@PathVariable Integer khachHangId) {
        return ResponseEntity.ok(donHangService.getOrdersByCustomerId(khachHangId));
    }

    @GetMapping("/order/{donHangId}")
    public ResponseEntity<DonHang> getOrderById(@PathVariable Integer donHangId) {
        Optional<DonHang> order = donHangService.getOrderById(donHangId);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ── ADMIN: lấy tất cả đơn ────────────────────────────────────────────────
    @GetMapping("/admin/orders")
    public ResponseEntity<List<DonHang>> getAllOrdersForAdmin() {
        return ResponseEntity.ok(donHangService.getAllOrders());
    }

    /**
     * PUT /api/checkout/admin/orders/{donHangId}/status?trangThaiId=2
     * Cập nhật trạng thái đơn hàng → tự động ghi lịch sử + gửi WebSocket.
     */
    @PutMapping("/admin/orders/{donHangId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer donHangId,
            @RequestParam Integer trangThaiId) {
        try {
            DonHang updated = donHangService.updateOrderStatus(donHangId, trangThaiId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ── LỊCH SỬ TRẠNG THÁI ĐƠN HÀNG ─────────────────────────────────────────
    /**
     * GET /api/checkout/order/{donHangId}/history
     * Trả về toàn bộ lịch sử thay đổi trạng thái của 1 đơn hàng (mới → cũ).
     */
    @GetMapping("/order/{donHangId}/history")
    public ResponseEntity<List<LichSuDonHang>> getOrderHistory(@PathVariable Integer donHangId) {
        return ResponseEntity.ok(donHangService.getLichSuDonHang(donHangId));
    }

    // ── THÔNG BÁO KHÁCH HÀNG ─────────────────────────────────────────────────
    /**
     * GET /api/checkout/notifications/{khachHangId}
     * Lấy tất cả thông báo của khách hàng (mới nhất trước).
     */
    @GetMapping("/notifications/{khachHangId}")
    public ResponseEntity<List<ThongBao>> getNotifications(@PathVariable Integer khachHangId) {
        return ResponseEntity.ok(
                thongBaoRepository.findByIdKHOrderByNgayGuiDesc(khachHangId));
    }

    /**
     * GET /api/checkout/notifications/{khachHangId}/unread-count
     * Đếm số thông báo chưa đọc.
     */
    @GetMapping("/notifications/{khachHangId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Integer khachHangId) {
        long count = thongBaoRepository.countByIdKHAndDaDocFalse(khachHangId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    /**
     * PUT /api/checkout/notifications/{khachHangId}/mark-all-read
     * Đánh dấu tất cả thông báo đã đọc.
     */
    @PutMapping("/notifications/{khachHangId}/mark-all-read")
    public ResponseEntity<?> markAllNotificationsRead(@PathVariable Integer khachHangId) {
        int updated = thongBaoRepository.markAllAsRead(khachHangId);
        return ResponseEntity.ok(Map.of("updated", updated, "message", "Đã đánh dấu tất cả đã đọc"));
    }

    /**
     * PUT /api/checkout/notifications/{idThongBao}/read
     * Đánh dấu 1 thông báo đã đọc.
     */
    @PutMapping("/notifications/{idThongBao}/read")
    public ResponseEntity<?> markNotificationRead(@PathVariable Integer idThongBao) {
        return thongBaoRepository.findById(idThongBao).map(tb -> {
            tb.setDaDoc(true);
            thongBaoRepository.save(tb);
            return ResponseEntity.ok(Map.of("message", "Đã đánh dấu đã đọc"));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
 