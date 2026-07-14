package com.mtbw.movieticketbooking.service.impl;

import com.mtbw.movieticketbooking.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private Path posterStoragePath;

    @PostConstruct
    public void init() {
        posterStoragePath = Paths.get(uploadDir, "posters").toAbsolutePath().normalize();
        try {
            Files.createDirectories(posterStoragePath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu poster: " + posterStoragePath, e);
        }
    }

    @Override
    public String savePosterFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Lấy phần mở rộng file gốc
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Tạo tên file duy nhất
        String newFilename = UUID.randomUUID().toString() + extension;

        try {
            Path targetPath = posterStoragePath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/posters/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file poster: " + e.getMessage(), e);
        }
    }
}
