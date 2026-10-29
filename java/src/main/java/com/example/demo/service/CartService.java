package com.example.demo.service;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final KhachHangRepository khachHangRepository;
    private final SanPhamRepository sanPhamRepository;

    @Transactional
    public Cart addToCart(Integer idKH, Integer idSP, Integer soLuong) {
        KhachHang khachHang = khachHangRepository.findById(idKH)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        SanPham sanPham = sanPhamRepository.findById(idSP)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        // Kiểm tra tồn kho
        if (sanPham.getSoLuong() < soLuong) {
            throw new RuntimeException("Số lượng sản phẩm không đủ");
        }

        CartId cartId = new CartId();
        cartId.setIdKh(idKH);
        cartId.setIdSp(idSP);
         // Kiểm tra đã có trong giỏ chưa
        Optional<Cart> existingCart = cartRepository.findById(cartId);

        Cart cart;
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            cart.setSoLuong(cart.getSoLuong() + soLuong);
        } else {
            cart = new Cart();
            cart.setId(cartId);
            cart.setKhachHang(khachHang);
            cart.setSanPham(sanPham);
            cart.setSoLuong(soLuong);
        }
        cart.setNgayThem(LocalDateTime.now());

        return cartRepository.save(cart);
    }

    
    public List<Cart> getCartItems(Integer idKH) {
        return cartRepository.findByKhachHang_IdKH(idKH);
    }
    
    // Lấy giỏ hàng với thông tin đầy đủ
    public List<CartItemDTO> getCartItemsDetail(Integer idKH) {
        List<Object[]> results = cartRepository.findCartItemsByIdKH(idKH);
        List<CartItemDTO> cartItems = new ArrayList<>();
        
        for (Object[] result : results) {
            CartItemDTO item = new CartItemDTO();
            item.setIdSp((Integer) result[0]);
            item.setTenSanPham((String) result[1]);
            item.setHinhAnh((String) result[2]);
            item.setSoLuong((Integer) result[3]);
            item.setGiaBan(new BigDecimal(result[4].toString()));
            item.setThanhTien(new BigDecimal(result[5].toString()));
            cartItems.add(item);
        }
        
        return cartItems;
    }
    
    @Transactional
    public void removeFromCart(Integer idKH, Integer idSP) {
        CartId cartId = new CartId();
        cartId.setIdKh(idKH);
        cartId.setIdSp(idSP);
        
        if (!cartRepository.existsById(cartId)) {
            throw new RuntimeException("Sản phẩm không tồn tại trong giỏ hàng");
        }
        
        cartRepository.deleteById(cartId);
    }
    
    @Transactional
    public Cart updateCartItemQuantity(Integer idKH, Integer idSP, Integer soLuong) {
        if (soLuong <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }
        
        CartId cartId = new CartId();
        cartId.setIdKh(idKH);
        cartId.setIdSp(idSP);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong giỏ hàng"));
        
        SanPham sanPham = cart.getSanPham();
        if (sanPham.getSoLuong() < soLuong) {
            throw new RuntimeException("Số lượng sản phẩm không đủ");
        }
        
        cart.setSoLuong(soLuong);
        cart.setNgayThem(LocalDateTime.now());
        
        return cartRepository.save(cart);
    }
    
    @Transactional
    public void clearCart(Integer idKH) {
        List<Cart> cartItems = cartRepository.findByKhachHang_IdKH(idKH);
        cartRepository.deleteAll(cartItems);
    }
} 