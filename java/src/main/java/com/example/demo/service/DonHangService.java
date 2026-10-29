package com.example.demo.service;

import com.example.demo.dto.CheckoutRequestDTO;
import com.example.demo.dto.CheckoutResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DonHangService {
    private final DonHangRepository donHangRepository;
    private final KhachHangRepository khachHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final PhuongThucVanChuyenRepository phuongThucVanChuyenRepository;
    private final DiaChiGiaoHangRepository diaChiGiaoHangRepository;
    private final MaKhuyenMaiRepository maKhuyenMaiRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final Random random = new Random();

    @Transactional
    public CheckoutResponseDTO createOrder(CheckoutRequestDTO request) {
        try {
            // Lấy thông tin khách hàng
            Optional<KhachHang> khachHangOptional = khachHangRepository.findById(request.getKhachHangId());
            if (khachHangOptional.isEmpty()) {
                return CheckoutResponseDTO.builder()
                        .success(false)
                        .message("Không tìm thấy khách hàng")
                        .build();
            }
            
            KhachHang khachHang = khachHangOptional.get();
            
            // Tạo đơn hàng mới
            DonHang donHang = new DonHang();
            donHang.setKhachHang(khachHang);
            
            // Tính tổng tiền sản phẩm từ request hoặc từ giỏ hàng
            BigDecimal tongTienSanPham = request.getTongTienSanPham();
            if (tongTienSanPham == null || tongTienSanPham.compareTo(BigDecimal.ZERO) <= 0) {
                // Tính tổng tiền từ giỏ hàng nếu không có trong request
                tongTienSanPham = calculateCartTotal(request.getKhachHangId());
            }
            donHang.setTongTienSanPham(tongTienSanPham);
            
            // Random phí vận chuyển từ 30000-50000
            BigDecimal phiVanChuyen = BigDecimal.valueOf(30000 + random.nextInt(20001));
            donHang.setPhiVanChuyen(phiVanChuyen);
            
            // Random giảm giá từ 50000-70000
            BigDecimal giamGia = BigDecimal.valueOf(50000 + random.nextInt(20001));
            donHang.setTongTienGiamGia(giamGia);
            
            // Tính tổng thanh toán
            BigDecimal tongThanhToan = tongTienSanPham.add(phiVanChuyen).subtract(giamGia);
            donHang.setTongThanhToan(tongThanhToan);
            
            // Thiết lập trạng thái đơn hàng (1 - Chờ xử lý)
            Optional<TrangThaiDonHang> trangThaiDonHang = trangThaiDonHangRepository.findById(1);
            trangThaiDonHang.ifPresent(donHang::setTrangThai);
            
            // Thiết lập phương thức thanh toán (1 - Tiền mặt)
            Optional<PhuongThucThanhToan> pttt = phuongThucThanhToanRepository.findById(1);
            pttt.ifPresent(donHang::setPhuongThucThanhToan);
            
            // Thiết lập phương thức vận chuyển (1 - Giao hàng tiêu chuẩn)
            Optional<PhuongThucVanChuyen> ptvc = phuongThucVanChuyenRepository.findById(1);
            ptvc.ifPresent(donHang::setPhuongThucVanChuyen);
            
            // Thiết lập địa chỉ giao hàng (1 - Địa chỉ mặc định)
            Optional<DiaChiGiaoHang> diaChiGiaoHang = diaChiGiaoHangRepository.findById(1);
            diaChiGiaoHang.ifPresent(donHang::setDiaChiGiaoHang);
            
            // Tổng hợp ghi chú khách hàng
            String ghiChuKhachHang = String.format("Địa chỉ: %s, %s, %s, %s. Ghi chú: %s", 
                request.getDiaChi(), 
                request.getPhuongXa(), 
                request.getQuanHuyen(), 
                request.getTinhThanh(), 
                request.getGhiChuDonHang());
            donHang.setGhiChuKhachHang(ghiChuKhachHang);
            
            // Thiết lập mã khuyến mãi (1 - Mã mặc định)
            Optional<MaKhuyenMai> maKhuyenMai = maKhuyenMaiRepository.findById(1);
            maKhuyenMai.ifPresent(donHang::setMaKhuyenMai);
            
            // Thiết lập ngày đặt hàng
            donHang.setNgayDat(new Date());
            
            // Tổng hợp thông tin khách hàng
            String thongTinKhachHang = String.format("%s %s, SĐT: %s, Email: %s", 
                request.getHoKhachHang(), 
                request.getTenKhachHang(), 
                request.getSoDienThoai(), 
                request.getEmail());
            donHang.setThongTinKhachHang(thongTinKhachHang);
            
            // Lưu đơn hàng
            DonHang savedOrder = donHangRepository.save(donHang);
            
            // Xóa giỏ hàng sau khi đặt hàng thành công
            cartService.clearCart(request.getKhachHangId());
            
            return CheckoutResponseDTO.builder()
                    .donHangId(savedOrder.getIdDonHang())
                    .tongTienSanPham(savedOrder.getTongTienSanPham())
                    .phiVanChuyen(savedOrder.getPhiVanChuyen())
                    .tongTienGiamGia(savedOrder.getTongTienGiamGia())
                    .tongThanhToan(savedOrder.getTongThanhToan())
                    .success(true)
                    .message("Đặt hàng thành công")
                    .build();
            
        } catch (Exception e) {
            return CheckoutResponseDTO.builder()
                    .success(false)
                    .message("Lỗi khi đặt hàng: " + e.getMessage())
                    .build();
        }
    }
    
    //Tính tổng tiền giỏ hàng
    private BigDecimal calculateCartTotal(Integer khachHangId) {
        List<Object[]> cartItems = cartRepository.findCartItemsByIdKH(khachHangId);
        BigDecimal total = BigDecimal.ZERO;
        
        for (Object[] item : cartItems) {
            BigDecimal thanhTien = (BigDecimal) item[5]; // thanhTien là vị trí thứ 6 trong mảng
            total = total.add(thanhTien);
        }
        
        return total;
    }
    
    public List<DonHang> getOrdersByCustomerId(Integer khachHangId) {
        return donHangRepository.findByKhachHang_IdKH(khachHangId);
    }
    
    public Optional<DonHang> getOrderById(Integer donHangId) {
        return donHangRepository.findById(donHangId);
    }
    
    // Lấy tất cả đơn hàng cho admin
    public List<DonHang> getAllOrders() {
        return donHangRepository.findAll();
    }
    
    // Cập nhật trạng thái đơn hàng
    @Transactional
    public DonHang updateOrderStatus(Integer donHangId, Integer trangThaiId) {
        Optional<DonHang> donHangOpt = donHangRepository.findById(donHangId);
        if (donHangOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đơn hàng với ID: " + donHangId);
        }
        
        Optional<TrangThaiDonHang> trangThaiOpt = trangThaiDonHangRepository.findById(trangThaiId);
        if (trangThaiOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy trạng thái với ID: " + trangThaiId);
        }
        
        DonHang donHang = donHangOpt.get();
        donHang.setTrangThai(trangThaiOpt.get());
        
        return donHangRepository.save(donHang);
    }
} 