package com.example.demo.config;

import com.example.demo.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ImageStorageService imageStorageService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path imagePath = imageStorageService.getImageStorageLocation();
        String locationUri = imagePath.toUri().toString();
        if (!locationUri.endsWith("/")) {
            locationUri += "/";
        }

        // Cho phép truy cập tĩnh trực tiếp qua: http://localhost:8080/images/{fileName}
        registry.addResourceHandler("/images/**")
                .addResourceLocations(locationUri);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}

