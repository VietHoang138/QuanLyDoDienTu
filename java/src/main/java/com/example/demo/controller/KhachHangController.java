package com.example.demo.controller;

import com.example.demo.dto.DangKyDto;
import com.example.demo.dto.DangNhapDto;
import com.example.demo.dto.ResponseDto;
import com.example.demo.dto.UserInfoDto;
import com.example.demo.dto.AdminUserInfoDto;
import com.example.demo.entity.KhachHang;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.service.KhachHangService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/khach-hang")
@CrossOrigin("*")
public class KhachHangController {

    @Autowired
    private KhachHangRepository khachHangRepository;
    
    @Autowired
    private KhachHangService khachHangService;
    
    //API đăng ký
    @PostMapping("/dang-ky")
    public ResponseEntity<ResponseDto<UserInfoDto>> dangKy(@Valid @RequestBody DangKyDto dangKyDto) {
        try {
            // Kiểm tra mật khẩu xác nhận
            if (!dangKyDto.getMatKhau().equals(dangKyDto.getXacNhanMatKhau())) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Mật khẩu xác nhận không khớp"));
            }

            // Kiểm tra email đã tồn tại chưa
            if (khachHangRepository.existsByEmail(dangKyDto.getEmail())) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Email đã được sử dụng"));
            }

            // Kiểm tra số điện thoại đã tồn tại chưa
            if (khachHangRepository.existsBySdt(dangKyDto.getSdt())) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Số điện thoại đã được sử dụng"));
            }

            // Tạo khách hàng mới
            KhachHang khachHang = new KhachHang();
            khachHang.setHoTen(dangKyDto.getHoTen());
            khachHang.setEmail(dangKyDto.getEmail());
            khachHang.setMatKhau(dangKyDto.getMatKhau());
            khachHang.setSdt(dangKyDto.getSdt());
            khachHang.setDiemTichLuy(0);
            khachHang.setTongChiTieu(BigDecimal.ZERO);
            khachHang.setTrangThai(true);
            khachHang.setNgayDangKy(new Date());

            // Lưu vào database
            KhachHang savedKhachHang = khachHangService.saveKhachHang(khachHang);

            // Trả về thông tin
            UserInfoDto userInfoDto = UserInfoDto.builder()
                    .idKH(savedKhachHang.getIdKH())
                    .hoTen(savedKhachHang.getHoTen())
                    .email(savedKhachHang.getEmail())
                    .sdt(savedKhachHang.getSdt())
                    .diemTichLuy(savedKhachHang.getDiemTichLuy())
                    .tongChiTieu(savedKhachHang.getTongChiTieu() != null ? savedKhachHang.getTongChiTieu().doubleValue() : 0.0)
                    .build();

            return ResponseEntity.ok(ResponseDto.success("Đăng ký thành công", userInfoDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Đăng ký thất bại: " + e.getMessage()));
        }
    }

    //API đăng nhập
    @PostMapping("/dang-nhap")
    public ResponseEntity<ResponseDto<UserInfoDto>> dangNhap(@RequestBody DangNhapDto dangNhapDto) {
        try {
            // Kiểm tra tài khoản admin đặc biệt
            if ("admin@techno.vn".equals(dangNhapDto.getEmail()) && "123456".equals(dangNhapDto.getMatKhau())) {
                // Tìm khách hàng admin
                Optional<KhachHang> optionalAdmin = khachHangService.findByEmail("admin@techno.vn");
                
                if (optionalAdmin.isPresent()) {
                    KhachHang admin = optionalAdmin.get();
                    
                    // Trả về thông tin admin
                    UserInfoDto adminInfo = UserInfoDto.builder()
                            .idKH(admin.getIdKH())
                            .hoTen(admin.getHoTen())
                            .email(admin.getEmail())
                            .vaiTro("ADMIN")
                            .build();
                    
                    return ResponseEntity.ok(ResponseDto.success("Đăng nhập thành công", adminInfo));
                }
            }
            
            // Xử lý đăng nhập thông thường
            Optional<KhachHang> optionalKhachHang = khachHangService.findByEmail(dangNhapDto.getEmail());
            
            if (optionalKhachHang.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Email không tồn tại"));
            }
            
            KhachHang khachHang = optionalKhachHang.get();
            
            // Kiểm tra mật khẩu
            if (!dangNhapDto.getMatKhau().equals(khachHang.getMatKhau())) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Mật khẩu không chính xác"));
            }
            
            // Kiểm tra trạng thái tài khoản
            if (!khachHang.getTrangThai()) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Tài khoản đã bị khóa"));
            }
            
            // Trả về thông tin người dùng
            UserInfoDto userInfoDto = UserInfoDto.builder()
                    .idKH(khachHang.getIdKH())
                    .hoTen(khachHang.getHoTen())
                    .email(khachHang.getEmail())
                    .sdt(khachHang.getSdt())
                    .diaChi(khachHang.getDiaChi())
                    .diemTichLuy(khachHang.getDiemTichLuy())
                    .tongChiTieu(khachHang.getTongChiTieu() != null ? khachHang.getTongChiTieu().doubleValue() : 0.0)
                    .vaiTro("USER")
                    .build();
            
            return ResponseEntity.ok(ResponseDto.success("Đăng nhập thành công", userInfoDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Đăng nhập thất bại: " + e.getMessage()));
        }
    }

    //API lấy thông tin khách hàng
    @GetMapping("/thong-tin/{idKH}")
    public ResponseEntity<ResponseDto<UserInfoDto>> getThongTin(@PathVariable Integer idKH) {
        try {
            // Tìm khách hàng theo id
            Optional<KhachHang> optionalKhachHang = khachHangService.getKhachHangById(idKH);
            
            if (optionalKhachHang.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Không tìm thấy thông tin khách hàng"));
            }
            
            KhachHang khachHang = optionalKhachHang.get();
            
            // Trả về thông tin người dùng
            UserInfoDto userInfoDto = UserInfoDto.builder()
                    .idKH(khachHang.getIdKH())
                    .hoTen(khachHang.getHoTen())
                    .email(khachHang.getEmail())
                    .sdt(khachHang.getSdt())
                    .diaChi(khachHang.getDiaChi())
                    .diemTichLuy(khachHang.getDiemTichLuy())
                    .tongChiTieu(khachHang.getTongChiTieu() != null ? khachHang.getTongChiTieu().doubleValue() : 0.0)
                    .build();
            
            return ResponseEntity.ok(ResponseDto.success("Lấy thông tin thành công", userInfoDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Lấy thông tin thất bại: " + e.getMessage()));
        }
    }

    // Lấy tất cả khách hàng
    @GetMapping("/all")
    public ResponseEntity<List<KhachHang>> getAllKhachHang() {
        try {
            List<KhachHang> khachHangList = khachHangService.getAllKhachHang();
            return ResponseEntity.ok(khachHangList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // API mới: Lấy tất cả khách hàng với đầy đủ thông tin bao gồm mật khẩu cho admin
    @GetMapping("/admin/all")
    public ResponseEntity<ResponseDto<List<AdminUserInfoDto>>> getAllKhachHangForAdmin() {
        try {
            List<KhachHang> khachHangList = khachHangService.getAllKhachHang();
            List<AdminUserInfoDto> adminUserInfoDtos = khachHangList.stream()
                    .map(khachHang -> AdminUserInfoDto.builder()
                            .idKH(khachHang.getIdKH())
                            .hoTen(khachHang.getHoTen())
                            .email(khachHang.getEmail())
                            .sdt(khachHang.getSdt())
                            .diaChi(khachHang.getDiaChi())
                            .matKhau(khachHang.getMatKhau())
                            .diemTichLuy(khachHang.getDiemTichLuy())
                            .tongChiTieu(khachHang.getTongChiTieu())
                            .trangThai(khachHang.getTrangThai())
                            .ngayDangKy(khachHang.getNgayDangKy())
                            .build())
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ResponseDto.success("Lấy danh sách người dùng thành công", adminUserInfoDtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Lấy danh sách người dùng thất bại: " + e.getMessage()));
        }
    }
    
    // Xóa khách hàng
    @DeleteMapping("/{idKH}")
    public ResponseEntity<ResponseDto<String>> deleteKhachHang(@PathVariable Integer idKH) {
        try {
            Optional<KhachHang> optionalKhachHang = khachHangService.getKhachHangById(idKH);
            
            if (optionalKhachHang.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Không tìm thấy khách hàng"));
            }
            
            khachHangService.deleteKhachHang(idKH);
            
            return ResponseEntity.ok(ResponseDto.success("Xóa khách hàng thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Xóa khách hàng thất bại: " + e.getMessage()));
        }
    }
    
    // Cập nhật trạng thái khách hàng
    @PutMapping("/{idKH}/status")
    public ResponseEntity<ResponseDto<KhachHang>> updateStatus(
            @PathVariable Integer idKH,
            @RequestBody Map<String, Boolean> statusUpdate) {
        try {
            Boolean trangThai = statusUpdate.get("trangThai");
            
            if (trangThai == null) {
                return ResponseEntity.badRequest().body(ResponseDto.error("Trạng thái không hợp lệ"));
            }
            
            KhachHang updatedKhachHang = khachHangService.updateStatus(idKH, trangThai);
            
            return ResponseEntity.ok(ResponseDto.success("Cập nhật trạng thái thành công", updatedKhachHang));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Cập nhật trạng thái thất bại: " + e.getMessage()));
        }
    }
} 