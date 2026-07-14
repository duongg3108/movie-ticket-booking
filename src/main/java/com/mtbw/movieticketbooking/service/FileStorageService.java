package com.mtbw.movieticketbooking.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Lưu file poster vào thư mục uploads/posters
     * @param file file ảnh poster
     * @return đường dẫn tương đối (ví dụ: /uploads/posters/abc123.jpg)
     */
    String savePosterFile(MultipartFile file);
}
