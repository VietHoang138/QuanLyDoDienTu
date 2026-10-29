package com.example.demo.controller;

import com.example.demo.dto.ResponseDto;
import com.example.demo.dto.WishlistItemDto;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.SanPham;
import com.example.demo.entity.SanPhamHinhAnh;
import com.example.demo.entity.Wishlist;
import com.example.demo.entity.WishlistId;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.SanPhamRepository;
import com.example.demo.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:5501", "http://127.0.0.1:5501"}, allowCredentials = "true")
public class WishlistController {
    private final WishlistRepository wishlistRepository;
    private final KhachHangRepository khachHangRepository;
    private final SanPhamRepository sanPhamRepository;

    /**
     * Lấy danh sách yêu thích của người dùng
     * @param idKH ID của khách hàng
     * @return Danh sách sản phẩm yêu thích
     */

    //API lấy danh sách yêu thích
    @GetMapping("/{idKH}")
    public ResponseEntity<ResponseDto<List<WishlistItemDto>>> getWishlist(@PathVariable Integer idKH) {
        try {
            // Kiểm tra khách hàng tồn tại
            Optional<KhachHang> khachHangOpt = khachHangRepository.findById(idKH);
            if (khachHangOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Không tìm thấy khách hàng"));
            }

            // Lấy danh sách yêu thích
            List<Wishlist> wishlistItems = wishlistRepository.findByIdKhachHangId(idKH);
            
            // Chuyển đổi thành DTO
            List<WishlistItemDto> result = wishlistItems.stream()
                .map(item -> {
                    SanPham sanPham = item.getSanPham();
                    String imageUrl = "https://placehold.co/300x300?text=No+Image";
                    
                    // Xử lý hình ảnh sản phẩm
                    if (sanPham.getHinhAnhList() != null && !sanPham.getHinhAnhList().isEmpty()) {
                        // Lấy URL hình ảnh đầu tiên nếu có
                        SanPhamHinhAnh firstImage = sanPham.getHinhAnhList().get(0);
                        if (firstImage != null && firstImage.getHinhAnh() != null) {
                            imageUrl = firstImage.getHinhAnh();
                        }
                    }
                    
                    return WishlistItemDto.builder()
                        .id(sanPham.getIdSp())
                        .name(sanPham.getTenSanPham())
                        .image(imageUrl)
                        .price(sanPham.getGiaBan().doubleValue()) // Chuyển BigDecimal sang Double
                        .build();
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(ResponseDto.success("Lấy danh sách yêu thích thành công", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Lấy danh sách yêu thích thất bại: " + e.getMessage()));
        }
    }

    /**
     * Thêm sản phẩm vào danh sách yêu thích
     * @param idKH ID của khách hàng
     * @param idSP ID của sản phẩm
     * @return Kết quả thao tác
     */

    //API thêm sản phẩm vào danh sách yêu thích
    @PostMapping("/add")
    public ResponseEntity<ResponseDto<Void>> addToWishlist(
            @RequestParam Integer idKH,
            @RequestParam Integer idSP) {
        try {
            // Kiểm tra khách hàng tồn tại
            Optional<KhachHang> khachHangOpt = khachHangRepository.findById(idKH);
            if (khachHangOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Không tìm thấy khách hàng"));
            }

            // Kiểm tra sản phẩm tồn tại
            Optional<SanPham> sanPhamOpt = sanPhamRepository.findById(idSP);
            if (sanPhamOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Không tìm thấy sản phẩm"));
            }

            // Kiểm tra xem sản phẩm đã có trong danh sách yêu thích chưa
            WishlistId wishlistId = new WishlistId(idKH, idSP);
            Optional<Wishlist> existingItem = wishlistRepository.findById(wishlistId);
            
            if (existingItem.isPresent()) {
                return ResponseEntity.ok(ResponseDto.success("Sản phẩm đã có trong danh sách yêu thích"));
            }

            // Thêm vào danh sách yêu thích
            Wishlist wishlistItem = new Wishlist();
            wishlistItem.setId(wishlistId);
            wishlistItem.setKhachHang(khachHangOpt.get());
            wishlistItem.setSanPham(sanPhamOpt.get());
            
            wishlistRepository.save(wishlistItem);

            return ResponseEntity.ok(ResponseDto.success("Đã thêm sản phẩm vào danh sách yêu thích"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Thêm vào danh sách yêu thích thất bại: " + e.getMessage()));
        }
    }

    /**
     * Xóa sản phẩm khỏi danh sách yêu thích
     * @param idKH ID của khách hàng
     * @param idSP ID của sản phẩm
     * @return Kết quả thao tác
     */
    @DeleteMapping("/remove")
    public ResponseEntity<ResponseDto<Void>> removeFromWishlist(
            @RequestParam Integer idKH,
            @RequestParam Integer idSP) {
        try {
            // Tạo ID cho wishlist item
            WishlistId wishlistId = new WishlistId(idKH, idSP);
            
            // Kiểm tra item có tồn tại không
            if (!wishlistRepository.existsById(wishlistId)) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Sản phẩm không có trong danh sách yêu thích"));
            }
            
            // Xóa item
            wishlistRepository.deleteById(wishlistId);
            
            return ResponseEntity.ok(ResponseDto.success("Đã xóa sản phẩm khỏi danh sách yêu thích"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Xóa khỏi danh sách yêu thích thất bại: " + e.getMessage()));
        }
    }

    /**
     * Xóa toàn bộ danh sách yêu thích của người dùng
     * @param idKH ID của khách hàng
     * @return Kết quả thao tác
     */
    @DeleteMapping("/clear/{idKH}")
    public ResponseEntity<ResponseDto<Void>> clearWishlist(@PathVariable Integer idKH) {
        try {
            // Kiểm tra khách hàng tồn tại
            Optional<KhachHang> khachHangOpt = khachHangRepository.findById(idKH);
            if (khachHangOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Không tìm thấy khách hàng"));
            }
            
            // Xóa tất cả các item của khách hàng
            List<Wishlist> wishlistItems = wishlistRepository.findByIdKhachHangId(idKH);
            wishlistRepository.deleteAll(wishlistItems);
            
            return ResponseEntity.ok(ResponseDto.success("Đã xóa toàn bộ danh sách yêu thích"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Xóa danh sách yêu thích thất bại: " + e.getMessage()));
        }
    }
} 