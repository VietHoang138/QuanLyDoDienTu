package com.example.demo.service;

import com.example.demo.dto.CheckoutRequestDTO;
import com.example.demo.dto.CheckoutResponseDTO;
import com.example.demo.dto.DonHangNotificationDTO;
import com.example.demo.dto.SanPhamDaBanDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

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
    private final SanPhamRepository           sanPhamRepository;
    private final ChiTietDonHangRepository    chiTietDonHangRepository;
    private final SanPhamHinhAnhRepository    sanPhamHinhAnhRepository;
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

            // ── Trừ tồn kho cho từng sản phẩm trong giỏ hàng ────────────────
            // Ưu tiên dùng chiTietDonHang từ request (frontend localStorage-based)
            // Fallback về DB cart nếu không có
            System.out.println("📦 chiTietDonHang nhận được: "
                    + (request.getChiTietDonHang() != null ? request.getChiTietDonHang().size() + " items" : "NULL"));

            boolean usedRequestItems = false;
            if (request.getChiTietDonHang() != null && !request.getChiTietDonHang().isEmpty()) {
                for (CheckoutRequestDTO.ChiTietDonHangDTO item : request.getChiTietDonHang()) {
                    if (item.getSanPhamId() == null || item.getSoLuong() == null) {
                        System.out.println("⚠️ Bỏ qua item null: sanPhamId=" + item.getSanPhamId() + ", soLuong=" + item.getSoLuong());
                        continue;
                    }
                    System.out.println("🔻 Trừ kho: sanPhamId=" + item.getSanPhamId() + ", soLuong=" + item.getSoLuong());
                    int updated = sanPhamRepository.decreaseStock(item.getSanPhamId(), item.getSoLuong());
                    System.out.println("   → updated=" + updated + (updated == 0 ? " ⚠️ KHÔNG ĐỦ KHO" : " ✅"));

                    // Lưu vào bảng CHI_TIET_DON_HANG
                    try {
                        Optional<SanPham> spOpt = sanPhamRepository.findById(item.getSanPhamId());
                        if (spOpt.isPresent()) {
                            ChiTietDonHang ctdh = new ChiTietDonHang();
                            ctdh.setDonHang(savedOrder);
                            ctdh.setSanPham(spOpt.get());
                            ctdh.setSoLuong(item.getSoLuong());
                            ctdh.setGiaBan(item.getDonGia() != null ? item.getDonGia() : spOpt.get().getGiaBan());
                            ctdh.setGiaGiam(BigDecimal.ZERO);
                            BigDecimal tt = item.getThanhTien() != null ? item.getThanhTien() :
                                    (ctdh.getGiaBan() != null ? ctdh.getGiaBan().multiply(BigDecimal.valueOf(item.getSoLuong())) : BigDecimal.ZERO);
                            ctdh.setThanhTien(tt);
                            chiTietDonHangRepository.save(ctdh);
                        }
                    } catch (Exception ex) {
                        System.err.println("⚠️ Lỗi lưu ChiTietDonHang: " + ex.getMessage());
                    }
                }
                usedRequestItems = true;
            }

            if (!usedRequestItems) {
                System.out.println("📦 Fallback về DB cart...");
                List<Cart> cartEntities = cartRepository.findByKhachHang_IdKH(request.getKhachHangId());
                System.out.println("📦 DB cart có " + cartEntities.size() + " items");
                for (Cart cartItem : cartEntities) {
                    Integer idSp    = cartItem.getSanPham().getIdSp();
                    Integer soLuong = cartItem.getSoLuong();
                    System.out.println("🔻 Trừ kho (DB): sanPhamId=" + idSp + ", soLuong=" + soLuong);
                    int updated = sanPhamRepository.decreaseStock(idSp, soLuong);
                    System.out.println("   → updated=" + updated + (updated == 0 ? " ⚠️ KHÔNG ĐỦ KHO" : " ✅"));

                    // Lưu vào bảng CHI_TIET_DON_HANG
                    try {
                        ChiTietDonHang ctdh = new ChiTietDonHang();
                        ctdh.setDonHang(savedOrder);
                        ctdh.setSanPham(cartItem.getSanPham());
                        ctdh.setSoLuong(soLuong);
                        ctdh.setGiaBan(cartItem.getSanPham().getGiaBan());
                        ctdh.setGiaGiam(BigDecimal.ZERO);
                        BigDecimal tt = cartItem.getSanPham().getGiaBan() != null
                                ? cartItem.getSanPham().getGiaBan().multiply(BigDecimal.valueOf(soLuong))
                                : BigDecimal.ZERO;
                        ctdh.setThanhTien(tt);
                        chiTietDonHangRepository.save(ctdh);
                    } catch (Exception ex) {
                        System.err.println("⚠️ Lỗi lưu ChiTietDonHang (DB): " + ex.getMessage());
                    }
                }
            }

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

    // ── Tổng hợp sản phẩm đã bán (đặt hàng thành công) ─────────────────────
    public List<SanPhamDaBanDTO> getSoldProducts() {
        List<ChiTietDonHang> ctdhList = chiTietDonHangRepository.findAllValidSoldItems();
        Map<Integer, SanPhamDaBanDTO> map = new LinkedHashMap<>();

        for (ChiTietDonHang ct : ctdhList) {
            SanPham sp = ct.getSanPham();
            if (sp == null) continue;
            Integer spId = sp.getIdSp();

            SanPhamDaBanDTO dto = map.get(spId);
            if (dto == null) {
                String imgUrl = null;
                List<String> imgUrls = sanPhamHinhAnhRepository.findHinhAnhUrlsByIdSp(spId);
                if (imgUrls != null && !imgUrls.isEmpty()) {
                    imgUrl = imgUrls.get(0);
                }

                String tenDanhMuc = (sp.getDanhMucCon() != null && sp.getDanhMucCon().getTenDanhMuc() != null)
                        ? sp.getDanhMucCon().getTenDanhMuc() : "Sản phẩm";

                dto = SanPhamDaBanDTO.builder()
                        .idSp(spId)
                        .tenSanPham(sp.getTenSanPham())
                        .hinhAnh(imgUrl)
                        .tenDanhMuc(tenDanhMuc)
                        .giaBan(sp.getGiaBan() != null ? sp.getGiaBan() : BigDecimal.ZERO)
                        .soLuongDaBan(0)
                        .tongTienBan(BigDecimal.ZERO)
                        .soLuongTonKho(sp.getSoLuong() != null ? sp.getSoLuong() : 0)
                        .soDonHang(0)
                        .danhSachDonHang(new ArrayList<>())
                        .build();
                map.put(spId, dto);
            }

            int qty = (ct.getSoLuong() != null && ct.getSoLuong() > 0) ? ct.getSoLuong() : 1;
            dto.setSoLuongDaBan(dto.getSoLuongDaBan() + qty);

            BigDecimal tt = ct.getThanhTien();
            if (tt == null && ct.getGiaBan() != null) {
                tt = ct.getGiaBan().multiply(BigDecimal.valueOf(qty));
            }
            if (tt == null && sp.getGiaBan() != null) {
                tt = sp.getGiaBan().multiply(BigDecimal.valueOf(qty));
            }
            if (tt == null) {
                tt = BigDecimal.ZERO;
            }
            dto.setTongTienBan(dto.getTongTienBan().add(tt));

            DonHang dh = ct.getDonHang();
            if (dh != null) {
                if (dto.getNgayBanGanNhat() == null || (dh.getNgayDat() != null && dh.getNgayDat().after(dto.getNgayBanGanNhat()))) {
                    dto.setNgayBanGanNhat(dh.getNgayDat());
                }

                String tenKH = "Khách hàng";
                if (dh.getKhachHang() != null && dh.getKhachHang().getHoTen() != null && !dh.getKhachHang().getHoTen().isBlank()) {
                    tenKH = dh.getKhachHang().getHoTen();
                } else if (dh.getThongTinKhachHang() != null && !dh.getThongTinKhachHang().isBlank()) {
                    tenKH = dh.getThongTinKhachHang().split(",")[0].trim();
                }

                String tenTrangThai = dh.getTrangThai() != null ? dh.getTrangThai().getTenTrangThai() : "Đã đặt hàng";
                Integer ttId = dh.getTrangThai() != null ? dh.getTrangThai().getIdTtdh() : 1;

                SanPhamDaBanDTO.DonHangItemDTO dhItem = SanPhamDaBanDTO.DonHangItemDTO.builder()
                        .idDonHang(dh.getIdDonHang())
                        .maDonHang("#DH" + String.format("%03d", dh.getIdDonHang()))
                        .tenKhachHang(tenKH)
                        .thongTinKhachHang(dh.getThongTinKhachHang())
                        .ngayDat(dh.getNgayDat())
                        .soLuong(qty)
                        .donGia(ct.getGiaBan() != null ? ct.getGiaBan() : sp.getGiaBan())
                        .thanhTien(tt)
                        .trangThaiId(ttId)
                        .tenTrangThai(tenTrangThai)
                        .build();

                dto.getDanhSachDonHang().add(dhItem);
            }
        }

        // Nếu chưa có dữ liệu trong bảng chi tiết, lấy danh sách demo từ các sản phẩm thực tế
        if (map.isEmpty()) {
            List<SanPham> allSp = sanPhamRepository.findAll();
            if (allSp != null && !allSp.isEmpty()) {
                int limit = Math.min(8, allSp.size());
                for (int i = 0; i < limit; i++) {
                    SanPham sp = allSp.get(i);
                    Integer spId = sp.getIdSp();
                    List<String> imgUrls = sanPhamHinhAnhRepository.findHinhAnhUrlsByIdSp(spId);
                    String imgUrl = (imgUrls != null && !imgUrls.isEmpty()) ? imgUrls.get(0) : null;
                    String tenDanhMuc = (sp.getDanhMucCon() != null && sp.getDanhMucCon().getTenDanhMuc() != null)
                            ? sp.getDanhMucCon().getTenDanhMuc() : "Thiết bị";
                    int qty = 1 + (i % 3);
                    BigDecimal price = sp.getGiaBan() != null ? sp.getGiaBan() : BigDecimal.ZERO;
                    BigDecimal total = price.multiply(BigDecimal.valueOf(qty));

                    SanPhamDaBanDTO dto = SanPhamDaBanDTO.builder()
                            .idSp(spId)
                            .tenSanPham(sp.getTenSanPham())
                            .hinhAnh(imgUrl)
                            .tenDanhMuc(tenDanhMuc)
                            .giaBan(price)
                            .soLuongDaBan(qty)
                            .tongTienBan(total)
                            .soLuongTonKho(sp.getSoLuong() != null ? sp.getSoLuong() : 0)
                            .soDonHang(1)
                            .ngayBanGanNhat(new Date(System.currentTimeMillis() - (long) (i + 1) * 86400000L))
                            .danhSachDonHang(new ArrayList<>())
                            .build();

                    dto.getDanhSachDonHang().add(SanPhamDaBanDTO.DonHangItemDTO.builder()
                            .idDonHang(100 + i)
                            .maDonHang("#DH" + (100 + i))
                            .tenKhachHang("Khách hàng #" + (i + 1))
                            .thongTinKhachHang("Đặt mua trực tuyến")
                            .ngayDat(dto.getNgayBanGanNhat())
                            .soLuong(qty)
                            .donGia(price)
                            .thanhTien(total)
                            .trangThaiId(4)
                            .tenTrangThai("Hoàn thành")
                            .build());

                    map.put(spId, dto);
                }
            }
        }

        // Cập nhật số đơn hàng cho từng sản phẩm
        for (SanPhamDaBanDTO dto : map.values()) {
            dto.setSoDonHang(dto.getDanhSachDonHang().size());
        }

        List<SanPhamDaBanDTO> result = new ArrayList<>(map.values());
        result.sort((a, b) -> Integer.compare(b.getSoLuongDaBan(), a.getSoLuongDaBan()));
        return result;
    }
}
