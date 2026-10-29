package com.example.demo.service;

import com.example.demo.entity.KhachHang;
import com.example.demo.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    /**
     * Lấy tất cả khách hàng
     * @return danh sách tất cả khách hàng
     */
    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAll();
    }

    /**
     * Lấy thông tin khách hàng theo ID
     * @param id ID của khách hàng
     * @return Optional chứa thông tin khách hàng
     */
    public Optional<KhachHang> getKhachHangById(Integer id) {
        return khachHangRepository.findById(id);
    }

    /**
     * Tìm khách hàng theo email
     * @param email Email khách hàng
     * @return Optional chứa thông tin khách hàng
     */
    public Optional<KhachHang> findByEmail(String email) {
        return khachHangRepository.findByEmail(email);
    }

    /**
     * Cập nhật trạng thái của khách hàng
     * @param id ID của khách hàng
     * @param trangThai Trạng thái mới
     * @return Khách hàng đã cập nhật
     */
    public KhachHang updateStatus(Integer id, Boolean trangThai) {
        Optional<KhachHang> optionalKhachHang = khachHangRepository.findById(id);
        if (optionalKhachHang.isPresent()) {
            KhachHang khachHang = optionalKhachHang.get();
            khachHang.setTrangThai(trangThai);
            return khachHangRepository.save(khachHang);
        }
        throw new RuntimeException("Không tìm thấy khách hàng với ID: " + id);
    }

    /**
     * Xóa khách hàng theo ID
     * @param id ID của khách hàng
     */
    public void deleteKhachHang(Integer id) {
        khachHangRepository.deleteById(id);
    }

    /**
     * Lưu thông tin khách hàng
     * @param khachHang Đối tượng khách hàng
     * @return Khách hàng đã lưu
     */
    public KhachHang saveKhachHang(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
    }
} 