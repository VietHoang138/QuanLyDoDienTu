package com.example.demo.controller;

import com.example.demo.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {

    @Autowired
    private ImageStorageService imageStorageService;

    /**
     * API phục vụ ảnh trực tiếp: GET /api/images/{fileName}
     * Ví dụ: http://localhost:8080/api/images/SP1_1.jpg
     */
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        Resource resource = imageStorageService.loadAsResource(fileName);
        if (resource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Tự động nhận diện MediaType
        String contentType = null;
        try {
            Path path = resource.getFile().toPath();
            contentType = Files.probeContentType(path);
        } catch (IOException ignored) {}

        if (contentType == null) {
            String lower = fileName.toLowerCase();
            if (lower.endsWith(".png")) {
                contentType = "image/png";
            } else if (lower.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (lower.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (lower.endsWith(".svg")) {
                contentType = "image/svg+xml";
            } else if (lower.endsWith(".avif")) {
                contentType = "image/avif";
            } else {
                contentType = "image/jpeg";
            }
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400, public")
                .body(resource);
    }

    /**
     * API upload ảnh: POST /api/images/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String savedFileName = imageStorageService.storeFile(file);
            return ResponseEntity.ok(savedFileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}

