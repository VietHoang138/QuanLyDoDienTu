package com.example.demo.controller;

import com.example.demo.dto.CheckoutRequestDTO;
import com.example.demo.dto.CheckoutResponseDTO;
import com.example.demo.entity.DonHang;
import com.example.demo.service.DonHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:5501", "http://127.0.0.1:5501"}, allowCredentials = "true")
public class CheckoutController {
    private final DonHangService donHangService;
    
    //API đặt hàng
    @PostMapping("/place-order")
    public ResponseEntity<CheckoutResponseDTO> placeOrder(@RequestBody CheckoutRequestDTO request) {
        CheckoutResponseDTO response = donHangService.createOrder(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    //API lấy danh sách đơn hàng theo id khách hàng
    @GetMapping("/orders/{khachHangId}")
    public ResponseEntity<List<DonHang>> getOrdersByCustomerId(@PathVariable Integer khachHangId) {
        List<DonHang> orders = donHangService.getOrdersByCustomerId(khachHangId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/order/{donHangId}")
    public ResponseEntity<DonHang> getOrderById(@PathVariable Integer donHangId) {
        Optional<DonHang> order = donHangService.getOrderById(donHangId);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/admin/orders")
    public ResponseEntity<List<DonHang>> getAllOrdersForAdmin() {
        List<DonHang> orders = donHangService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/admin/orders/{donHangId}/status")
    public ResponseEntity<DonHang> updateOrderStatus(
            @PathVariable Integer donHangId, 
            @RequestParam Integer trangThaiId) {
        try {
            DonHang updatedOrder = donHangService.updateOrderStatus(donHangId, trangThaiId);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
} 