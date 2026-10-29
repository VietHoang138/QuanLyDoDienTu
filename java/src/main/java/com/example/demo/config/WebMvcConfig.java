package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // Đường dẫn tuyệt đối đến thư mục Image trên máy
    private static final String IMAGE_DIR = "file:///d:/2.2_Ky2_Nam2/LapTrinhJavaNangCao/btcuoiky/Image/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ URL /images/** -> thư mục Image trên ổ đĩa
        registry.addResourceHandler("/images/**")
                .addResourceLocations(IMAGE_DIR);
    }
}

