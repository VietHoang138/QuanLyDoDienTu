package com.example.demo.controller;

import com.example.demo.dto.SanPhamDTO;
import com.example.demo.dto.SanPhamListDTO;
import com.example.demo.dto.SanPhamChiTietDTO;
import com.example.demo.dto.SanPhamDaBanDTO;
import com.example.demo.entity.SanPham;
import com.example.demo.service.SanPhamService;
import com.example.demo.service.DonHangService;
import com.example.demo.service.ImageStorageService;
import com.example.demo.service.CloudinaryService;
import com.example.demo.repository.SanPhamRepository;
import com.example.demo.repository.SanPhamHinhAnhRepository;
import com.example.demo.service.WebSocketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/sanpham")
@CrossOrigin("*")
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired(required = false)
    private CloudinaryService cloudinaryService;
    
    @Autowired
    private SanPhamRepository sanPhamRepository;
    
    @Autowired
    private SanPhamHinhAnhRepository sanPhamHinhAnhRepository;
    
    @Autowired
    private WebSocketService webSocketService;

    @Autowired(required = false)
    private DonHangService donHangService;

    @GetMapping("/da-ban")
    public ResponseEntity<List<SanPhamDaBanDTO>> getSanPhamDaBan() {
        if (donHangService != null) {
            return ResponseEntity.ok(donHangService.getSoldProducts());
        }
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping
    public ResponseEntity<List<SanPhamListDTO>> getAllSanPham() {
        List<SanPhamListDTO> sanPhams = sanPhamService.getAllSanPham();
        return new ResponseEntity<>(sanPhams, HttpStatus.OK);
    }
    
    //API láº¥y sáº£n pháº©m má»›i nháº¥t
    @GetMapping("/latest")
    public ResponseEntity<List<SanPhamListDTO>> getLatestProducts() {
        List<SanPhamListDTO> sanPhams = sanPhamService.getLatestProducts();
        return new ResponseEntity<>(sanPhams, HttpStatus.OK);
    }

    //API láº¥y chi tiáº¿t sáº£n pháº©m
    @GetMapping("/chitiet/{id}")
    public ResponseEntity<SanPhamChiTietDTO> getSanPhamChiTiet(@PathVariable Integer id) {
        return sanPhamService.getSanPhamChiTiet(id)
                .map(chiTiet -> new ResponseEntity<>(chiTiet, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //API láº¥y sáº£n pháº©m theo id
    @GetMapping("/{id}")
    public ResponseEntity<SanPham> getSanPhamById(@PathVariable Integer id) {
        return sanPhamService.getSanPhamById(id)
                .map(sanPham -> new ResponseEntity<>(sanPham, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //search
    @GetMapping("/search")
    public ResponseEntity<List<SanPhamListDTO>> searchSanPham(@RequestParam String keyword) {
        List<SanPhamListDTO> sanPhams = sanPhamService.searchSanPham(keyword);
        return new ResponseEntity<>(sanPhams, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SanPham> createSanPham(@Valid @RequestBody SanPhamDTO sanPhamDTO) {
        try {
            SanPham createdSanPham = sanPhamService.themSanPham(sanPhamDTO);
            
            // Gá»­i WebSocket notification cho táº¥t cáº£ client
            if (createdSanPham != null) {
                // Chuyá»ƒn Ä‘á»•i sang SanPhamListDTO Ä‘á»ƒ gá»­i notification
                SanPhamListDTO notificationDTO = convertToListDTO(createdSanPham);
                webSocketService.notifyProductAdded(notificationDTO);
                
                System.out.println("âœ… ÄĂ£ thĂªm sáº£n pháº©m vĂ  gá»­i WebSocket notification: " + createdSanPham.getTenSanPham());
            }
            
            return new ResponseEntity<>(createdSanPham, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("âŒ Lá»—i khi thĂªm sáº£n pháº©m: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Helper method Ä‘á»ƒ chuyá»ƒn Ä‘á»•i SanPham sang SanPhamListDTO
    private SanPhamListDTO convertToListDTO(SanPham sanPham) {
        SanPhamListDTO dto = new SanPhamListDTO();
        dto.setIdSp(sanPham.getIdSp());
        dto.setTenSanPham(sanPham.getTenSanPham());
        dto.setMoTaNgan(sanPham.getMoTaNgan());
        dto.setGiaBan(sanPham.getGiaBan());
        dto.setSoLuong(sanPham.getSoLuong());
        dto.setTrangThai(sanPham.getTrangThai());
        
        if (sanPham.getDanhMucCon() != null) {
            dto.setDanhMucConId(sanPham.getDanhMucCon().getIdDanhMuc());
            dto.setTenDanhMuc(sanPham.getDanhMucCon().getTenDanhMuc());
        }
        
        // Láº¥y hĂ¬nh áº£nh
        List<String> hinhAnhUrls = sanPhamHinhAnhRepository.findHinhAnhUrlsByIdSp(sanPham.getIdSp());
        dto.setHinhAnhUrls(sanPhamService.formatHinhAnhUrls(hinhAnhUrls));
        
        return dto;
    }

    @PutMapping("/{id}")
    public ResponseEntity<SanPhamListDTO> updateSanPham(@PathVariable Integer id, @Valid @RequestBody SanPhamListDTO sanPhamListDTO) {
        try {
            System.out.println("=== API PUT /api/sanpham/" + id + " ===");
            System.out.println("Dá»¯ liá»‡u nháº­n Ä‘Æ°á»£c: " + sanPhamListDTO.toString());
            
            SanPhamListDTO updatedSanPham = sanPhamService.updateSanPhamFromDTO(id, sanPhamListDTO);
            
            if (updatedSanPham != null) {
                // Gá»­i WebSocket notification cho táº¥t cáº£ client
                webSocketService.notifyProductUpdated(updatedSanPham);
                
                System.out.println("âœ… ÄĂ£ cáº­p nháº­t sáº£n pháº©m vĂ  gá»­i WebSocket notification: " + updatedSanPham.getTenSanPham());
                return ResponseEntity.ok(updatedSanPham);
            } else {
                System.err.println("âŒ KhĂ´ng tĂ¬m tháº¥y sáº£n pháº©m vá»›i ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("âŒ Lá»—i khi cáº­p nháº­t sáº£n pháº©m: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/entity/{id}")
    public ResponseEntity<SanPham> updateSanPhamEntity(@PathVariable Integer id, @RequestBody SanPham sanPham) {
        SanPham updatedSanPham = sanPhamService.updateSanPham(id, sanPham);
        if (updatedSanPham != null) {
            return new ResponseEntity<>(updatedSanPham, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSanPham(@PathVariable Integer id) {
        sanPhamService.deleteSanPham(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "prefix", required = false, defaultValue = "SP") String prefix,
            @RequestParam(value = "index", required = false, defaultValue = "1") int index) {
        try {
            String imageUrl = imageStorageService.storeFile(file, prefix, index);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/debug/{id}")
    public ResponseEntity<String> debugSanPham(@PathVariable Integer id) {
        try {
            sanPhamService.debugSanPham(id);
            return ResponseEntity.ok("Debug completed for product ID: " + id + ". Check server logs for details.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Debug failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/test/status")
    public ResponseEntity<String> testAllProductsStatus() {
        try {
            // Láº¥y táº¥t cáº£ sáº£n pháº©m khĂ´ng phĂ¢n biá»‡t tráº¡ng thĂ¡i
            List<SanPham> allProducts = sanPhamRepository.findAll();
            StringBuilder result = new StringBuilder();
            
            result.append("=== KIá»‚M TRA TRáº NG THĂI Táº¤T Cáº¢ Sáº¢N PHáº¨M ===\n");
            result.append("Tá»•ng sá»‘ sáº£n pháº©m trong DB: ").append(allProducts.size()).append("\n\n");
            
            int activeCount = 0;
            int inactiveCount = 0;
            int nullCount = 0;
            
            for (SanPham sp : allProducts) {
                Boolean status = sp.getTrangThai();
                String statusStr;
                if (status == null) {
                    statusStr = "NULL";
                    nullCount++;
                } else if (status) {
                    statusStr = "TRUE (active)";
                    activeCount++;
                } else {
                    statusStr = "FALSE (inactive)";
                    inactiveCount++;
                }
                
                result.append(String.format("ID %d - %s - TrangThai: %s\n", 
                    sp.getIdSp(), sp.getTenSanPham(), statusStr));
            }
            
            result.append("\n=== THá»NG KĂ ===\n");
            result.append("Sáº£n pháº©m ACTIVE (true): ").append(activeCount).append("\n");
            result.append("Sáº£n pháº©m INACTIVE (false): ").append(inactiveCount).append("\n");
            result.append("Sáº£n pháº©m NULL: ").append(nullCount).append("\n");
            
            // Test query getAllSanPham
            List<SanPhamListDTO> activeProducts = sanPhamService.getAllSanPham();
            result.append("\nSáº£n pháº©m tráº£ vá» tá»« getAllSanPham(): ").append(activeProducts.size()).append("\n");
            
            System.out.println(result.toString());
            return ResponseEntity.ok(result.toString());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Test failed: " + e.getMessage());
        }
    }

    @GetMapping("/all-no-filter")
    public ResponseEntity<List<SanPhamListDTO>> getAllSanPhamNoFilter() {
        List<SanPhamListDTO> sanPhams = sanPhamService.getAllSanPhamWithoutFilter();
        return new ResponseEntity<>(sanPhams, HttpStatus.OK);
    }

    /**
     * GET /api/sanpham/danhmuc/{id}?sort=newest|price-asc|price-desc
     * Láº¥y sáº£n pháº©m theo danh má»¥c, cĂ³ há»— trá»£ sáº¯p xáº¿p
     */
    @GetMapping("/danhmuc/{id}")
    public ResponseEntity<List<SanPhamListDTO>> getSanPhamByDanhMuc(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "newest") String sort) {
        List<SanPhamListDTO> sanPhams = sanPhamService.getSanPhamByDanhMuc(id, sort);
        return new ResponseEntity<>(sanPhams, HttpStatus.OK);
    }

    /**
     * GET /api/sanpham/all?sort=newest|price-asc|price-desc
     * Láº¥y táº¥t cáº£ sáº£n pháº©m cĂ³ há»— trá»£ sáº¯p xáº¿p
     */
    @GetMapping("/all")
    public ResponseEntity<List<SanPhamListDTO>> getAllSanPhamSorted(
            @RequestParam(defaultValue = "newest") String sort) {
        List<SanPhamListDTO> sanPhams = sanPhamService.getAllSanPhamSorted(sort);
        return new ResponseEntity<>(sanPhams, HttpStatus.OK);
    }
} 