package com.example.demo.controller;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartId;
import com.example.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:5501", "http://127.0.0.1:5501"}, allowCredentials = "true")
public class CartController {
    private final CartService cartService;

    //API thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestParam Integer idKH,
            @RequestParam Integer idSP,
            @RequestParam Integer soLuong) {
        try {
            Cart cart = cartService.addToCart(idKH, idSP, soLuong);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //API lấy danh sách sản phẩm trong giỏ hàng
    @GetMapping("/{idKH}")
    public ResponseEntity<List<Cart>> getCartItems(@PathVariable Integer idKH) {
        List<Cart> cartItems = cartService.getCartItems(idKH);
        return ResponseEntity.ok(cartItems);
    }

    //API lấy danh sách sản phẩm trong giỏ hàng với thông tin đầy đủ
    @GetMapping("/detail/{idKH}")
    public ResponseEntity<List<CartItemDTO>> getCartItemsDetail(@PathVariable Integer idKH) {
        List<CartItemDTO> cartItems = cartService.getCartItemsDetail(idKH);
        return ResponseEntity.ok(cartItems);
    }

    //API xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(
            @RequestParam Integer idKH,
            @RequestParam Integer idSP) {
        try {
            cartService.removeFromCart(idKH, idSP);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //API cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/update")
    public ResponseEntity<?> updateCartItemQuantity(
            @RequestParam Integer idKH,
            @RequestParam Integer idSP,
            @RequestParam Integer soLuong) {
        try {
            Cart cart = cartService.updateCartItemQuantity(idKH, idSP, soLuong);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 