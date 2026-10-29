package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ImageStorageService {

    @Value("${app.image.dir:d:/2.2_Ky2_Nam2/LapTrinhJavaNangCao/btcuoiky/Image}")
    private String configuredImageDir;

    /**
     * Xác định thư mục chứa ảnh trên máy tính
     */
    public Path getImageStorageLocation() {
        // 1. Thử đường dẫn đã cấu hình
        if (configuredImageDir != null && !configuredImageDir.trim().isEmpty()) {
            Path path = Paths.get(configuredImageDir.trim()).normalize();
            if (Files.exists(path)) {
                return path;
            }
        }

        // 2. Thử đường dẫn tương đối ../Image (khi chạy từ thư mục java)
        Path relPath1 = Paths.get(System.getProperty("user.dir"), "..", "Image").normalize();
        if (Files.exists(relPath1)) {
            return relPath1;
        }

        // 3. Thử đường dẫn tương đối ./Image (khi chạy từ thư mục gốc btcuoiky)
        Path relPath2 = Paths.get(System.getProperty("user.dir"), "Image").normalize();
        if (Files.exists(relPath2)) {
            return relPath2;
        }

        // 4. Mặc định tạo/sử dụng configured path
        Path defaultPath = Paths.get(configuredImageDir != null && !configuredImageDir.trim().isEmpty() 
                ? configuredImageDir.trim() 
                : "d:/2.2_Ky2_Nam2/LapTrinhJavaNangCao/btcuoiky/Image").normalize();
        try {
            if (!Files.exists(defaultPath)) {
                Files.createDirectories(defaultPath);
            }
        } catch (IOException ignored) {}
        return defaultPath;
    }

    /**
     * Tải file ảnh dưới dạng Resource để trả về client
     */
    public Resource loadAsResource(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }

        // Lấy tên file đơn thuần nếu có chứa đường dẫn URL
        String cleanFileName = fileName.trim();
        if (cleanFileName.contains("/")) {
            cleanFileName = cleanFileName.substring(cleanFileName.lastIndexOf("/") + 1);
        }
        if (cleanFileName.contains("\\")) {
            cleanFileName = cleanFileName.substring(cleanFileName.lastIndexOf("\\") + 1);
        }

        try {
            Path imageDir = getImageStorageLocation();
            Path filePath = imageDir.resolve(cleanFileName).normalize();

            // Bảo mật: ngăn path traversal
            if (!filePath.startsWith(imageDir)) {
                return null;
            }

            // Nếu file không tìm thấy theo đúng tên, thử tìm case-insensitive (không phân biệt hoa thường)
            if (!Files.exists(filePath)) {
                final String targetName = cleanFileName;
                try (Stream<Path> stream = Files.list(imageDir)) {
                    filePath = stream
                            .filter(p -> p.getFileName().toString().equalsIgnoreCase(targetName))
                            .findFirst()
                            .orElse(filePath);
                } catch (IOException ignored) {}
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
        } catch (MalformedURLException e) {
            return null;
        }

        return null;
    }

    /**
     * Lưu ảnh upload từ client vào thư mục Image
     */
    public String storeFile(MultipartFile file) throws IOException {
        Path imageDir = getImageStorageLocation();
        if (!Files.exists(imageDir)) {
            Files.createDirectories(imageDir);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
        Path targetLocation = imageDir.resolve(fileName).normalize();
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}

