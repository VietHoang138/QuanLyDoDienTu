package com.example.demo.controller;

import com.example.demo.dto.SanPhamChiTietDTO;
import com.example.demo.dto.SanPhamListDTO;
import com.example.demo.entity.DanhMuc;
import com.example.demo.service.DanhMucService;
import com.example.demo.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/report/sanpham")
public class SanPhamReportController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private DanhMucService danhMucService;

    /**
     * Báo cáo danh sách sản phẩm (có lọc theo từ khóa, danh mục, trạng thái tồn kho)
     */
    @GetMapping
    public String baoCaoDanhSachSanPham(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer danhMucId,
            @RequestParam(required = false, defaultValue = "all") String trangThai,
            Model model) {

        // Lấy toàn bộ sản phẩm
        List<SanPhamListDTO> allProducts = sanPhamService.getAllSanPham();
        if (allProducts == null) {
            allProducts = List.of();
        }

        // Lọc danh sách theo các tiêu chí
        List<SanPhamListDTO> filtered = allProducts.stream()
                .filter(sp -> {
                    // Lọc từ khóa (tên hoặc id)
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        String kw = keyword.toLowerCase().trim();
                        boolean matchName = sp.getTenSanPham() != null && sp.getTenSanPham().toLowerCase().contains(kw);
                        boolean matchId = sp.getIdSp() != null && sp.getIdSp().toString().contains(kw);
                        boolean matchMota = sp.getMoTaNgan() != null && sp.getMoTaNgan().toLowerCase().contains(kw);
                        if (!matchName && !matchId && !matchMota) {
                            return false;
                        }
                    }

                    // Lọc theo danh mục
                    if (danhMucId != null) {
                        if (sp.getDanhMucConId() == null || !sp.getDanhMucConId().equals(danhMucId)) {
                            return false;
                        }
                    }

                    // Lọc theo trạng thái tồn kho
                    if ("con-hang".equalsIgnoreCase(trangThai)) {
                        return sp.getSoLuong() != null && sp.getSoLuong() > 0;
                    } else if ("het-hang".equalsIgnoreCase(trangThai)) {
                        return sp.getSoLuong() == null || sp.getSoLuong() <= 0;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        // Tính toán các chỉ số thống kê tổng hợp
        int tongSoSanPham = filtered.size();
        int tongSoLuongTon = 0;
        BigDecimal tongGiaTriTonKho = BigDecimal.ZERO;
        int soSanPhamHetHang = 0;

        for (SanPhamListDTO sp : filtered) {
            int sl = sp.getSoLuong() != null ? sp.getSoLuong() : 0;
            tongSoLuongTon += sl;

            if (sl <= 0) {
                soSanPhamHetHang++;
            }

            if (sp.getGiaBan() != null && sl > 0) {
                BigDecimal giaTri = sp.getGiaBan().multiply(BigDecimal.valueOf(sl));
                tongGiaTriTonKho = tongGiaTriTonKho.add(giaTri);
            }
        }

        // Lấy danh mục để hiển thị lên form lọc
        List<DanhMuc> danhMucList = danhMucService.getAllDanhMuc();
        String tenDanhMucDaChon = null;
        if (danhMucId != null) {
            tenDanhMucDaChon = danhMucList.stream()
                    .filter(dm -> dm.getIdDanhMuc().equals(danhMucId))
                    .map(DanhMuc::getTenDanhMuc)
                    .findFirst()
                    .orElse(null);
        }

        // Thông tin chung của báo cáo
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String ngayLap = LocalDateTime.now().format(formatter);
        String maBaoCao = String.valueOf(System.currentTimeMillis()).substring(7);

        // Đẩy dữ liệu vào Model
        model.addAttribute("sanPhams", filtered);
        model.addAttribute("tongSoSanPham", tongSoSanPham);
        model.addAttribute("tongSoLuongTon", tongSoLuongTon);
        model.addAttribute("tongGiaTriTonKho", tongGiaTriTonKho);
        model.addAttribute("soSanPhamHetHang", soSanPhamHetHang);
        model.addAttribute("danhMucList", danhMucList);
        model.addAttribute("tenDanhMucDaChon", tenDanhMucDaChon);
        model.addAttribute("ngayLap", ngayLap);
        model.addAttribute("maBaoCao", maBaoCao);

        return "reportSanPham";
    }

    /**
     * Báo cáo thống kê chi tiết của một sản phẩm cụ thể
     */
    @GetMapping("/{id}")
    public String baoCaoChiTietSanPham(@PathVariable Integer id, Model model) {
        Optional<SanPhamChiTietDTO> chiTietOpt = sanPhamService.getSanPhamChiTiet(id);

        if (chiTietOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy sản phẩm với mã: #" + id);
            return "redirect:/report/sanpham";
        }

        SanPhamChiTietDTO sp = chiTietOpt.get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String ngayLap = LocalDateTime.now().format(formatter);

        model.addAttribute("sp", sp);
        model.addAttribute("ngayLap", ngayLap);

        return "reportChiTietSanPham";
    }
}

