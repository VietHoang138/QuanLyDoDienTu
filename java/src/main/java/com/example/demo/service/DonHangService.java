package com.example.demo.service;

import com.example.demo.dto.CheckoutRequestDTO;
import com.example.demo.dto.CheckoutResponseDTO;
import com.example.demo.dto.DonHangNotificationDTO;
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
    private final DonHangRepository           donHangRepository;
    private final KhachHangRepository         khachHangRepository;
    private final TrangThaiDonHangRepository  trangThaiDonHangRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final PhuongThucVanChuyenRepository phuongThucVanChuyenRepository;
    private final DiaChiGiaoHangRepository    diaChiGiaoHangRepository;
    private final MaKhuyenMaiRepository       maKhuyenMaiRepository;
    private final CartRepository              cartRepository;
    private final CartService                 cartService;
    private final LichSuDonHangRepository     lichSuDonHangRepository;
    private final ThongBaoRepository          thongBaoRepository;
    private final WebSocketService            webSocketService;
    private final Random random = new Random();

    @Transactional
    public CheckoutResponseDTO createOrder(CheckoutRequestDTO request) {
        try {
            Optional<KhachHang> khachHangOptional = khachHangRepository.findById(request.getKhachHangId());
            if (khachHangOptional.isEmpty()) {
                return CheckoutResponseDTO.builder()
                        .success(false)
                        .message("Không tìm thấy khách hàng")
                        .build();
            }

            KhachHang khachHang = khachHangOptional.get();

            DonHang donHang = new DonHang();
            donHang.setKhachHang(khachHang);

            BigDecimal tongTienSanPham = request.getTongTienSanPham();
            if (tongTienSanPham == null || tongTienSanPham.compareTo(BigDecimal.ZERO) <= 0) {
                tongTienSanPham = calculateCartTotal(request.getKhachHangId());
            }
            donHang.setTongTienSanPham(tongTienSanPham);

            BigDecimal phiVanChuyen = BigDecimal.valueOf(30000 + random.nextInt(20001));
            donHang.setPhiVanChuyen(phiVanChuyen);

            BigDecimal giamGia = BigDecimal.valueOf(50000 + random.nextInt(20001));
            donHang.setTongTienGiamGia(giamGia);

            BigDecimal tongThanhToan = tongTienSanPham.add(phiVanChuyen).subtract(giamGia);
            donHang.setTongThanhToan(tongThanhToan);

            // Trạng thái mặc định: 1 - Chờ xử lý
            TrangThaiDonHang trangThaiMacDinh = null;
            Optional<TrangThaiDonHang> trangThaiDonHang = trangThaiDonHangRepository.findById(1);
            if (trangThaiDonHang.isPresent()) {
                trangThaiMacDinh = trangThaiDonHang.get();
                donHang.setTrangThai(trangThaiMacDinh);
            }

            Optional<PhuongThucThanhToan> pttt = phuongThucThanhToanRepository.findById(1);
            pttt.ifPresent(donHang::setPhuongThucThanhToan);

            Optional<PhuongThucVanChuyen> ptvc = phuongThucVanChuyenRepository.findById(1);
            ptvc.ifPresent(donHang::setPhuongThucVanChuyen);

            Optional<DiaChiGiaoHang> diaChiGiaoHang = diaChiGiaoHangRepository.findById(1);
            diaChiGiaoHang.ifPresent(donHang::setDiaChiGiaoHang);

            String ghiChu = String.format("Địa chỉ: %s, %s, %s, %s. Ghi chú: %s",
                    request.getDiaChi(), request.getPhuongXa(),
                    request.getQuanHuyen(), request.getTinhThanh(),
                    request.getGhiChuDonHang());
            donHang.setGhiChuKhachHang(ghiChu);

            Optional<MaKhuyenMai> maKhuyenMai = maKhuyenMaiRepository.findById(1);
            maKhuyenMai.ifPresent(donHang::setMaKhuyenMai);

            donHang.setNgayDat(new Date());

            String thongTinKH = String.format("%s %s, SĐT: %s, Email: %s",
                    request.getHoKhachHang(), request.getTenKhachHang(),
                    request.getSoDienThoai(), request.getEmail());
            donHang.setThongTinKhachHang(thongTinKH);

            DonHang savedOrder = donHangRepository.save(donHang);

            // ── Lưu lịch sử trạng thái ban đầu ──────────────────────────────
            if (trangThaiMacDinh != null) {
                LichSuDonHang ls = new LichSuDonHang();
                ls.setDonHang(savedOrder);
                ls.setTrangThai(trangThaiMacDinh);
                ls.setNgayTaoDon(new Date());
                lichSuDonHangRepository.save(ls);
            }

            // ── Lưu thông báo vào DB (cho khách hàng) ────────────────────────
            ThongBao thongBaoKH = new ThongBao();
            thongBaoKH.setIdKH(khachHang.getIdKH());
            thongBaoKH.setTieuDe("Đặt hàng thành công #" + savedOrder.getIdDonHang());
            thongBaoKH.setNoiDung("Đơn hàng của bạn đã được tiếp nhận và đang chờ xử lý. "
                    + "Tổng thanh toán: " + String.format("%,.0f đ", tongThanhToan));
            thongBaoKH.setNgayGui(new Date());
            thongBaoKH.setDaDoc(false);
            thongBaoRepository.save(thongBaoKH);

            // ── Gửi WebSocket đến Admin (/topic/orders) ──────────────────────
            DonHangNotificationDTO notification = DonHangNotificationDTO.builder()
                    .type("NEW_ORDER")
                    .donHangId(savedOrder.getIdDonHang())
                    .khachHangId(khachHang.getIdKH())
                    .tenKhachHang(khachHang.getHoTen())
                    .thongTinKhachHang(thongTinKH)
                    .tongThanhToan(tongThanhToan)
                    .ngayDat(savedOrder.getNgayDat())
                    .trangThaiId(trangThaiMacDinh != null ? trangThaiMacDinh.getIdTtdh() : 1)
                    .tenTrangThai(trangThaiMacDinh != null ? trangThaiMacDinh.getTenTrangThai() : "Chờ xử lý")
                    .message("Đơn hàng mới #" + savedOrder.getIdDonHang() + " từ " + khachHang.getHoTen())
                    .timestamp(System.currentTimeMillis())
                    .build();

            webSocketService.notifyNewOrder(notification);

            // Xóa giỏ hàng
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

    private BigDecimal calculateCartTotal(Integer khachHangId) {
        List<Object[]> cartItems = cartRepository.findCartItemsByIdKH(khachHangId);
        BigDecimal total = BigDecimal.ZERO;
        for (Object[] item : cartItems) {
            BigDecimal thanhTien = (BigDecimal) item[5];
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

    public List<DonHang> getAllOrders() {
        return donHangRepository.findAll();
    }

    // ── Cập nhật trạng thái + ghi lịch sử + WebSocket ────────────────────────
    @Transactional
    public DonHang updateOrderStatus(Integer donHangId, Integer trangThaiId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + donHangId));

        TrangThaiDonHang trangThaiMoi = trangThaiDonHangRepository.findById(trangThaiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái ID: " + trangThaiId));

        // Lưu tên trạng thái cũ để gửi WebSocket
        String trangThaiCu = donHang.getTrangThai() != null
                ? donHang.getTrangThai().getTenTrangThai()
                : "Không rõ";

        donHang.setTrangThai(trangThaiMoi);
        DonHang savedOrder = donHangRepository.save(donHang);

        // ── Ghi lịch sử trạng thái ──────────────────────────────────────────
        LichSuDonHang ls = new LichSuDonHang();
        ls.setDonHang(savedOrder);
        ls.setTrangThai(trangThaiMoi);
        ls.setNgayTaoDon(new Date());
        lichSuDonHangRepository.save(ls);

        // ── Lưu thông báo cho khách hàng ────────────────────────────────────
        Integer idKH = donHang.getKhachHang() != null ? donHang.getKhachHang().getIdKH() : null;
        if (idKH != null) {
            ThongBao tb = new ThongBao();
            tb.setIdKH(idKH);
            tb.setTieuDe("Đơn hàng #" + donHangId + " cập nhật trạng thái");
            tb.setNoiDung("Đơn hàng của bạn đã được cập nhật: "
                    + trangThaiCu + " → " + trangThaiMoi.getTenTrangThai());
            tb.setNgayGui(new Date());
            tb.setDaDoc(false);
            thongBaoRepository.save(tb);
        }

        // ── Gửi WebSocket (/topic/orders + /topic/orders/{idKH}) ────────────
        DonHangNotificationDTO notification = DonHangNotificationDTO.builder()
                .type("STATUS_UPDATED")
                .donHangId(donHangId)
                .khachHangId(idKH)
                .tenKhachHang(donHang.getKhachHang() != null ? donHang.getKhachHang().getHoTen() : "")
                .tongThanhToan(donHang.getTongThanhToan())
                .ngayDat(donHang.getNgayDat())
                .trangThaiId(trangThaiMoi.getIdTtdh())
                .tenTrangThai(trangThaiMoi.getTenTrangThai())
                .trangThaiCu(trangThaiCu)
                .message("Đơn #" + donHangId + ": " + trangThaiCu + " → " + trangThaiMoi.getTenTrangThai())
                .timestamp(System.currentTimeMillis())
                .build();

        webSocketService.notifyOrderStatusUpdated(notification);

        return savedOrder;
    }

    // ── Lịch sử trạng thái của 1 đơn hàng ─────────────────────────────────
    public List<LichSuDonHang> getLichSuDonHang(Integer donHangId) {
        return lichSuDonHangRepository.findByDonHang_IdDonHangOrderByNgayTaoDonAsc(donHangId);
    }
}
