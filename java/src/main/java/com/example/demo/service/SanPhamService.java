package com.example.demo.service;

import com.example.demo.dto.SanPhamDTO;
import com.example.demo.dto.SanPhamListDTO;
import com.example.demo.dto.SanPhamChiTietDTO;
import com.example.demo.entity.DanhMuc;
import com.example.demo.entity.SanPham;
import com.example.demo.entity.SanPhamHinhAnh;
import com.example.demo.entity.SanPhamDungLuong;
import com.example.demo.entity.SanPhamMauSac;
import com.example.demo.repository.DanhMucRepository;
import com.example.demo.repository.SanPhamHinhAnhRepository;
import com.example.demo.repository.SanPhamRepository;
import com.example.demo.repository.SanPhamDungLuongRepository;
import com.example.demo.repository.SanPhamMauSacRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SanPhamService {
    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private SanPhamHinhAnhRepository sanPhamHinhAnhRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;
    
    @Autowired
    private SanPhamDungLuongRepository dungLuongRepository;
    
    @Autowired
    private SanPhamMauSacRepository mauSacRepository;
    
    @PersistenceContext
    private EntityManager entityManager;


    //API lấy danh sách tìm kiếm sản phẩm
    @Transactional(readOnly = true)
    public List<SanPhamListDTO> getAllSanPham() {
        // Sử dụng Native Query để lấy dữ liệu theo đúng yêu cầu
        String sql = "SELECT s.IdSP, s.TenSanPham, s.MoTaNgan, s.GiaBan, s.SoLuong, " +
                     "s.DanhMucConId, dm.TenDanhMuc, s.TrangThai " +
                     "FROM SAN_PHAM s " +
                     "INNER JOIN DANH_MUC dm ON s.DanhMucConId = dm.IdDanhMuc " +
                     "WHERE s.TrangThai = 1 " +
                     "ORDER BY s.IdSP DESC";
        
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        
        List<SanPhamListDTO> sanPhamDTOList = new ArrayList<>();
        
        for (Object[] result : results) {
            SanPhamListDTO dto = new SanPhamListDTO();
            dto.setIdSp((Integer) result[0]);
            dto.setTenSanPham((String) result[1]);
            dto.setMoTaNgan((String) result[2]);
            dto.setGiaBan((BigDecimal) result[3]);
            dto.setSoLuong((Integer) result[4]);
            dto.setDanhMucConId((Integer) result[5]);
            dto.setTenDanhMuc((String) result[6]);
            dto.setTrangThai((Boolean) result[7]);
            
            // Lấy danh sách hình ảnh cho sản phẩm
            List<String> hinhAnhUrls = sanPhamHinhAnhRepository.findHinhAnhUrlsByIdSp(dto.getIdSp());
            dto.setHinhAnhUrls(hinhAnhUrls);
            
            sanPhamDTOList.add(dto);
        }
        
        return sanPhamDTOList;
    }
    
    //API lấy danh sách sản phẩm
    @Transactional(readOnly = true)
    public List<SanPhamListDTO> getAllSanPhamWithoutFilter() {
        // Query KHÔNG CÓ ĐIỀU KIỆN LỌC để debug
        String sql = "SELECT s.IdSP, s.TenSanPham, s.MoTaNgan, s.GiaBan, s.SoLuong, " +
                     "s.DanhMucConId, ISNULL(dm.TenDanhMuc, 'Không có danh mục') as TenDanhMuc, s.TrangThai " +
                     "FROM SAN_PHAM s " +
                     "LEFT JOIN DANH_MUC dm ON s.DanhMucConId = dm.IdDanhMuc " +
                     "ORDER BY s.IdSP DESC";
        
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        
        List<SanPhamListDTO> sanPhamDTOList = new ArrayList<>();
        
        for (Object[] result : results) {
            SanPhamListDTO dto = new SanPhamListDTO();
            dto.setIdSp((Integer) result[0]);
            dto.setTenSanPham((String) result[1]);
            dto.setMoTaNgan((String) result[2]);
            dto.setGiaBan((BigDecimal) result[3]);
            dto.setSoLuong((Integer) result[4]);
            dto.setDanhMucConId((Integer) result[5]);
            dto.setTenDanhMuc((String) result[6]);
            dto.setTrangThai((Boolean) result[7]);
            
            // Lấy danh sách hình ảnh cho sản phẩm
            List<String> hinhAnhUrls = sanPhamHinhAnhRepository.findHinhAnhUrlsByIdSp(dto.getIdSp());
            dto.setHinhAnhUrls(hinhAnhUrls);
            
            sanPhamDTOList.add(dto);
        }
        
        return sanPhamDTOList;
    }

    @Transactional(readOnly = true)
    public List<SanPhamListDTO> getLatestProducts() {
        // Sử dụng cùng phương thức với getAllSanPham vì query đã có sắp xếp theo ID giảm dần
        return getAllSanPham();
    }

    public Optional<SanPham> getSanPhamById(Integer id) {
        return sanPhamRepository.findById(id);
    }

    @Transactional
    public SanPham themSanPham(SanPhamDTO sanPhamDTO) {
        // Tìm danh mục
        DanhMuc danhMuc = danhMucRepository.findById(sanPhamDTO.getDanhMucConId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục có ID: " + sanPhamDTO.getDanhMucConId()));

        // Tìm dung lượng nếu có
        SanPhamDungLuong dungLuong = null;
        if (sanPhamDTO.getDungLuongId() != null) {
            dungLuong = dungLuongRepository.findById(sanPhamDTO.getDungLuongId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy dung lượng có ID: " + sanPhamDTO.getDungLuongId()));
        }

        // Tìm màu sắc nếu có
        SanPhamMauSac mauSac = null;
        if (sanPhamDTO.getMauSacId() != null) {
            mauSac = mauSacRepository.findById(sanPhamDTO.getMauSacId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy màu sắc có ID: " + sanPhamDTO.getMauSacId()));
        }

        // Tạo sản phẩm mới
        SanPham sanPham = new SanPham();
        sanPham.setTenSanPham(sanPhamDTO.getTenSanPham());
        sanPham.setMoTaNgan(sanPhamDTO.getMoTaNgan());
        sanPham.setGiaBan(sanPhamDTO.getGiaBan());
        sanPham.setDanhMucCon(danhMuc);
        sanPham.setSoLuong(sanPhamDTO.getSoLuong() != null ? sanPhamDTO.getSoLuong() : 0);
        sanPham.setDungLuong(dungLuong);
        sanPham.setMauSac(mauSac);
        sanPham.setTrangThai(true);

        // Lưu sản phẩm để có ID
        SanPham savedSanPham = sanPhamRepository.save(sanPham);

        // Tạo và lưu hình ảnh sản phẩm nếu có
        if (sanPhamDTO.getHinhAnhUrls() != null && !sanPhamDTO.getHinhAnhUrls().isEmpty()) {
            List<SanPhamHinhAnh> hinhAnhList = new ArrayList<>();
            
            for (String hinhAnhUrl : sanPhamDTO.getHinhAnhUrls()) {
                SanPhamHinhAnh hinhAnh = new SanPhamHinhAnh();
                hinhAnh.setSanPham(savedSanPham);
                hinhAnh.setHinhAnh(hinhAnhUrl);
                hinhAnhList.add(hinhAnh);
            }
            
            sanPhamHinhAnhRepository.saveAll(hinhAnhList);
        }

        return savedSanPham;
    }

    @Transactional
    public SanPham updateSanPham(Integer id, SanPham sanPhamDetails) {
        Optional<SanPham> sanPham = sanPhamRepository.findById(id);
        if (sanPham.isPresent()) {
            SanPham existingSanPham = sanPham.get();
            existingSanPham.setTenSanPham(sanPhamDetails.getTenSanPham());
            existingSanPham.setMoTaNgan(sanPhamDetails.getMoTaNgan());
            existingSanPham.setGiaBan(sanPhamDetails.getGiaBan());
            existingSanPham.setSoLuong(sanPhamDetails.getSoLuong());
            
            // Chỉ cập nhật các trường khác nếu có giá trị
            if (sanPhamDetails.getDanhMucCha() != null) {
                existingSanPham.setDanhMucCha(sanPhamDetails.getDanhMucCha());
            }
            if (sanPhamDetails.getDanhMucCon() != null) {
                existingSanPham.setDanhMucCon(sanPhamDetails.getDanhMucCon());
            }
            if (sanPhamDetails.getThuongHieu() != null) {
                existingSanPham.setThuongHieu(sanPhamDetails.getThuongHieu());
            }
            if (sanPhamDetails.getBaoHanh() != null) {
                existingSanPham.setBaoHanh(sanPhamDetails.getBaoHanh());
            }
            if (sanPhamDetails.getCanNang() != null) {
                existingSanPham.setCanNang(sanPhamDetails.getCanNang());
            }
            if (sanPhamDetails.getKichThuoc() != null) {
                existingSanPham.setKichThuoc(sanPhamDetails.getKichThuoc());
            }
            if (sanPhamDetails.getMauSac() != null) {
                existingSanPham.setMauSac(sanPhamDetails.getMauSac());
            }
            if (sanPhamDetails.getDungLuong() != null) {
                existingSanPham.setDungLuong(sanPhamDetails.getDungLuong());
            }
            if (sanPhamDetails.getTrangThai() != null) {
                existingSanPham.setTrangThai(sanPhamDetails.getTrangThai());
            }
            
            return sanPhamRepository.save(existingSanPham);
        }
        return null;
    }

    public void deleteSanPham(Integer id) {
        sanPhamRepository.deleteById(id);
    }

    //API lấy chi tiết sản phẩm
    @Transactional(readOnly = true)
    public Optional<SanPhamChiTietDTO> getSanPhamChiTiet(Integer id) {
        String sql = "SELECT s.IdSP, s.TenSanPham, s.MoTaNgan, s.GiaBan, s.SoLuong, " +
                    "dm.TenDanhMuc, spms.TenMau, spdl.DungLuong " +
                    "FROM SAN_PHAM s " +
                    "INNER JOIN DANH_MUC dm ON dm.IdDanhMuc = s.DanhMucConId " +
                    "LEFT JOIN SANPHAM_MAUSAC spms ON spms.IdMau = s.MauSac " +
                    "LEFT JOIN SANPHAM_DUNGLUONG spdl ON spdl.IdDungLuong = s.DungLuong " +
                    "WHERE s.IdSP = :idSp";

        try {
            Object[] result = (Object[]) entityManager.createNativeQuery(sql)
                    .setParameter("idSp", id)
                    .getSingleResult();

            if (result != null) {
                SanPhamChiTietDTO dto = new SanPhamChiTietDTO();
                dto.setIdSp((Integer) result[0]);
                dto.setTenSanPham((String) result[1]);
                dto.setMoTaNgan((String) result[2]);
                dto.setGiaBan((BigDecimal) result[3]);
                dto.setSoLuong((Integer) result[4]);
                dto.setTenDanhMuc((String) result[5]);
                dto.setTenMau((String) result[6]);
                dto.setDungLuong((String) result[7]);

                // Lấy danh sách hình ảnh
                List<String> hinhAnhUrls = sanPhamHinhAnhRepository.findHinhAnhUrlsByIdSp(id);
                dto.setHinhAnhUrls(hinhAnhUrls);

                return Optional.of(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Transactional(readOnly = true)
    public List<SanPhamListDTO> searchSanPham(String keyword) {
        String sql = "SELECT s.IdSP, s.TenSanPham, s.MoTaNgan, s.GiaBan, s.SoLuong, " +
                     "s.DanhMucConId, dm.TenDanhMuc, s.TrangThai " +
                     "FROM SAN_PHAM s " +
                     "INNER JOIN DANH_MUC dm ON s.DanhMucConId = dm.IdDanhMuc " +
                     "WHERE s.TrangThai = 1 " +
                     "AND (s.TenSanPham LIKE :keyword OR s.MoTaNgan LIKE :keyword) " +
                     "ORDER BY s.IdSP DESC";
        
        Query query = entityManager.createNativeQuery(sql)
                    .setParameter("keyword", "%" + keyword + "%");
        
        List<Object[]> results = query.getResultList();
        
        List<SanPhamListDTO> sanPhamDTOList = new ArrayList<>();
        
        for (Object[] result : results) {
            SanPhamListDTO dto = new SanPhamListDTO();
            dto.setIdSp((Integer) result[0]);
            dto.setTenSanPham((String) result[1]);
            dto.setMoTaNgan((String) result[2]);
            dto.setGiaBan((BigDecimal) result[3]);
            dto.setSoLuong((Integer) result[4]);
            dto.setDanhMucConId((Integer) result[5]);
            dto.setTenDanhMuc((String) result[6]);
            dto.setTrangThai((Boolean) result[7]);
            
            // Lấy danh sách hình ảnh cho sản phẩm
            List<String> hinhAnhUrls = sanPhamHinhAnhRepository.findHinhAnhUrlsByIdSp(dto.getIdSp());
            dto.setHinhAnhUrls(hinhAnhUrls);
            
            sanPhamDTOList.add(dto);
        }
        
        return sanPhamDTOList;
    }

    @Transactional
    public SanPhamListDTO updateSanPhamFromDTO(Integer id, SanPhamListDTO sanPhamDTO) {
        try {
            System.out.println("=== Bắt đầu cập nhật sản phẩm ID: " + id + " ===");
            System.out.println("Dữ liệu nhận được: " + sanPhamDTO.toString());
            
            Optional<SanPham> sanPhamOpt = sanPhamRepository.findById(id);
            if (sanPhamOpt.isPresent()) {
                SanPham existingSanPham = sanPhamOpt.get();
                
                System.out.println("Sản phẩm hiện tại: " + existingSanPham.getTenSanPham());
                System.out.println("TrangThai hiện tại: " + existingSanPham.getTrangThai());
                System.out.println("TrangThai từ DTO: " + sanPhamDTO.getTrangThai());
                
                // CHỈ CẬP NHẬT CÁC TRƯỜNG CẦN THIẾT - KHÔNG ĐỘNG VÀO CÁC TRƯỜNG KHÁC
                if (sanPhamDTO.getTenSanPham() != null) {
                    existingSanPham.setTenSanPham(sanPhamDTO.getTenSanPham());
                }
                if (sanPhamDTO.getMoTaNgan() != null) {
                    existingSanPham.setMoTaNgan(sanPhamDTO.getMoTaNgan());
                }
                if (sanPhamDTO.getGiaBan() != null) {
                    existingSanPham.setGiaBan(sanPhamDTO.getGiaBan());
                }
                if (sanPhamDTO.getSoLuong() != null) {
                    existingSanPham.setSoLuong(sanPhamDTO.getSoLuong());
                }
                
                // ĐẢM BẢO TRẠNG THÁI LUÔN LÀ TRUE - KHÔNG BAO GIỜ ĐỂ NULL HOẶC FALSE
                existingSanPham.setTrangThai(true);
                System.out.println("Force TrangThai = true");
                
                // Cập nhật danh mục nếu có thay đổi và danh mục hiện tại không null
                if (sanPhamDTO.getDanhMucConId() != null) {
                    if (existingSanPham.getDanhMucCon() == null || 
                        !sanPhamDTO.getDanhMucConId().equals(existingSanPham.getDanhMucCon().getIdDanhMuc())) {
                        DanhMuc newDanhMuc = danhMucRepository.findById(sanPhamDTO.getDanhMucConId())
                            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục có ID: " + sanPhamDTO.getDanhMucConId()));
                        existingSanPham.setDanhMucCon(newDanhMuc);
                        System.out.println("Cập nhật danh mục: " + newDanhMuc.getTenDanhMuc());
                    }
                }
                
                System.out.println("TrangThai trước khi lưu: " + existingSanPham.getTrangThai());
                
                // Lưu sản phẩm và force commit
                SanPham savedSanPham = sanPhamRepository.save(existingSanPham);
                entityManager.flush(); // Force immediate flush to database
                entityManager.clear(); // Clear persistence context để force reload
                
                System.out.println("Đã lưu sản phẩm thành công - ID: " + savedSanPham.getIdSp());
                System.out.println("TrangThai sau khi lưu: " + savedSanPham.getTrangThai());
                
                // XÁC MINH BẰN CÁCH RELOAD TỪ DATABASE
                Optional<SanPham> verifyProduct = sanPhamRepository.findById(id);
                if (verifyProduct.isPresent()) {
                    SanPham verified = verifyProduct.get();
                    System.out.println("✅ XÁC MINH: Sản phẩm trong DB - Tên: " + verified.getTenSanPham() + ", TrangThai: " + verified.getTrangThai());
                    
                    // Sử dụng dữ liệu đã verify để tạo response
                    savedSanPham = verified;
                } else {
                    System.out.println("❌ CẢNH BÁO: Không thể xác minh sản phẩm trong DB");
                }
                
                // Tạo DTO trả về với đầy đủ thông tin từ dữ liệu đã verify
                SanPhamListDTO resultDTO = new SanPhamListDTO();
                resultDTO.setIdSp(savedSanPham.getIdSp());
                resultDTO.setTenSanPham(savedSanPham.getTenSanPham());
                resultDTO.setMoTaNgan(savedSanPham.getMoTaNgan());
                resultDTO.setGiaBan(savedSanPham.getGiaBan());
                resultDTO.setSoLuong(savedSanPham.getSoLuong());
                resultDTO.setTrangThai(savedSanPham.getTrangThai()); // ĐẢM BẢO TRẢ VỀ TRẠNG THÁI
                
                // Xử lý an toàn cho danh mục
                if (savedSanPham.getDanhMucCon() != null) {
                    resultDTO.setDanhMucConId(savedSanPham.getDanhMucCon().getIdDanhMuc());
                    resultDTO.setTenDanhMuc(savedSanPham.getDanhMucCon().getTenDanhMuc());
                }
                
                // Lấy danh sách hình ảnh từ database
                List<String> hinhAnhUrls = sanPhamHinhAnhRepository.findHinhAnhUrlsByIdSp(savedSanPham.getIdSp());
                System.out.println("Số lượng hình ảnh tìm thấy: " + (hinhAnhUrls != null ? hinhAnhUrls.size() : 0));
                if (hinhAnhUrls != null && !hinhAnhUrls.isEmpty()) {
                    System.out.println("Danh sách hình ảnh: " + hinhAnhUrls.toString());
                    resultDTO.setHinhAnhUrls(hinhAnhUrls);
                } else {
                    System.out.println("Không tìm thấy hình ảnh từ DB, sử dụng danh sách rỗng");
                    resultDTO.setHinhAnhUrls(new ArrayList<>());
                }
                
                System.out.println("=== Hoàn thành cập nhật sản phẩm ===");
                System.out.println("DTO trả về: " + resultDTO.toString());
                return resultDTO;
            } else {
                System.out.println("Không tìm thấy sản phẩm với ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }
        return null;
    }

    public List<String> testGetImages(Integer idSp) {
        List<String> images = sanPhamHinhAnhRepository.findHinhAnhUrlsByIdSp(idSp);
        System.out.println("Test service - Số lượng hình ảnh cho sản phẩm " + idSp + ": " + (images != null ? images.size() : 0));
        if (images != null && !images.isEmpty()) {
            System.out.println("Danh sách hình ảnh: " + images);
        }
        return images;
    }

    @Transactional(readOnly = true)
    public void debugSanPham(Integer idSp) {
        System.out.println("=== DEBUG SẢN PHẨM ID: " + idSp + " ===");
        
        // Kiểm tra sản phẩm có tồn tại không
        Optional<SanPham> sanPhamOpt = sanPhamRepository.findById(idSp);
        if (sanPhamOpt.isPresent()) {
            SanPham sp = sanPhamOpt.get();
            System.out.println("✅ Sản phẩm TỒN TẠI trong DB");
            System.out.println("- ID: " + sp.getIdSp());
            System.out.println("- Tên: " + sp.getTenSanPham());
            System.out.println("- TrangThai: " + sp.getTrangThai());
            System.out.println("- DanhMucCon: " + (sp.getDanhMucCon() != null ? sp.getDanhMucCon().getIdDanhMuc() : "NULL"));
            
            // Kiểm tra với query getAllSanPham
            String debugSql = "SELECT s.IdSP, s.TenSanPham, s.TrangThai, s.DanhMucConId " +
                             "FROM SAN_PHAM s " +
                             "WHERE s.IdSP = :idSp";
            
            Query debugQuery = entityManager.createNativeQuery(debugSql)
                    .setParameter("idSp", idSp);
            
            List<Object[]> debugResults = debugQuery.getResultList();
            if (!debugResults.isEmpty()) {
                Object[] result = debugResults.get(0);
                System.out.println("📋 THÔNG TIN RAW từ DB:");
                System.out.println("- IdSP: " + result[0]);
                System.out.println("- TenSanPham: " + result[1]);
                System.out.println("- TrangThai: " + result[2]);
                System.out.println("- DanhMucConId: " + result[3]);
                
                // Kiểm tra điều kiện trong getAllSanPham
                Boolean trangThai = (Boolean) result[2];
                Integer danhMucConId = (Integer) result[3];
                
                if (trangThai == null || !trangThai) {
                    System.out.println("❌ LÝ DO BỊ LỌC: TrangThai = " + trangThai + " (không = 1)");
                } else if (danhMucConId == null) {
                    System.out.println("❌ LÝ DO BỊ LỌC: DanhMucConId = NULL (không thể JOIN với DANH_MUC)");
                } else {
                    System.out.println("✅ Sản phẩm ĐẠT điều kiện lọc trong getAllSanPham");
                }
            } else {
                System.out.println("❌ KHÔNG TÌM THẤY trong query raw");
            }
        } else {
            System.out.println("❌ Sản phẩm KHÔNG TỒN TẠI trong DB");
        }
        
        System.out.println("=== KẾT THÚC DEBUG ===");
    }

    /**
     * Lấy sản phẩm theo danh mục (lọc theo idDanhMuc)
     * Hỗ trợ sắp xếp: newest, price-asc, price-desc, default (phổ biến = ID giảm dần)
     */
    @Transactional(readOnly = true)
    public List<SanPhamListDTO> getSanPhamByDanhMuc(Integer idDanhMuc, String sortBy) {
        String orderClause;
        switch (sortBy != null ? sortBy : "newest") {
            case "price-asc":  orderClause = "s.GiaBan ASC";  break;
            case "price-desc": orderClause = "s.GiaBan DESC"; break;
            case "newest":
            default:           orderClause = "s.IdSP DESC";   break;
        }

        String sql = "SELECT s.IdSP, s.TenSanPham, s.MoTaNgan, s.GiaBan, s.SoLuong, " +
                     "s.DanhMucConId, dm.TenDanhMuc, s.TrangThai " +
                     "FROM SAN_PHAM s " +
                     "INNER JOIN DANH_MUC dm ON s.DanhMucConId = dm.IdDanhMuc " +
                     "WHERE s.TrangThai = 1 AND s.DanhMucConId = :idDanhMuc " +
                     "ORDER BY " + orderClause;

        Query query = entityManager.createNativeQuery(sql)
                .setParameter("idDanhMuc", idDanhMuc);

        List<Object[]> results = query.getResultList();
        List<SanPhamListDTO> list = new ArrayList<>();

        for (Object[] result : results) {
            SanPhamListDTO dto = new SanPhamListDTO();
            dto.setIdSp((Integer) result[0]);
            dto.setTenSanPham((String) result[1]);
            dto.setMoTaNgan((String) result[2]);
            dto.setGiaBan((BigDecimal) result[3]);
            dto.setSoLuong((Integer) result[4]);
            dto.setDanhMucConId((Integer) result[5]);
            dto.setTenDanhMuc((String) result[6]);
            dto.setTrangThai((Boolean) result[7]);
            dto.setHinhAnhUrls(sanPhamHinhAnhRepository.findHinhAnhUrlsByIdSp(dto.getIdSp()));
            list.add(dto);
        }
        return list;
    }

    /**
     * Lấy tất cả sản phẩm có hỗ trợ sắp xếp (dùng cho trang "Tất cả sản phẩm")
     */
    @Transactional(readOnly = true)
    public List<SanPhamListDTO> getAllSanPhamSorted(String sortBy) {
        String orderClause;
        switch (sortBy != null ? sortBy : "newest") {
            case "price-asc":  orderClause = "s.GiaBan ASC";  break;
            case "price-desc": orderClause = "s.GiaBan DESC"; break;
            default:           orderClause = "s.IdSP DESC";   break;
        }

        String sql = "SELECT s.IdSP, s.TenSanPham, s.MoTaNgan, s.GiaBan, s.SoLuong, " +
                     "s.DanhMucConId, dm.TenDanhMuc, s.TrangThai " +
                     "FROM SAN_PHAM s " +
                     "INNER JOIN DANH_MUC dm ON s.DanhMucConId = dm.IdDanhMuc " +
                     "WHERE s.TrangThai = 1 " +
                     "ORDER BY " + orderClause;

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        List<SanPhamListDTO> list = new ArrayList<>();

        for (Object[] result : results) {
            SanPhamListDTO dto = new SanPhamListDTO();
            dto.setIdSp((Integer) result[0]);
            dto.setTenSanPham((String) result[1]);
            dto.setMoTaNgan((String) result[2]);
            dto.setGiaBan((BigDecimal) result[3]);
            dto.setSoLuong((Integer) result[4]);
            dto.setDanhMucConId((Integer) result[5]);
            dto.setTenDanhMuc((String) result[6]);
            dto.setTrangThai((Boolean) result[7]);
            dto.setHinhAnhUrls(sanPhamHinhAnhRepository.findHinhAnhUrlsByIdSp(dto.getIdSp()));
            list.add(dto);
        }
        return list;
    }
} 