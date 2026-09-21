package com.oilcommerce.fileupload.service;

import com.oilcommerce.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Slf4j @Service
public class FileUploadService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg","image/png","image/webp");
    private static final long MAX_SIZE = 5 * 1024 * 1024L;

    public Map<String,String> uploadFile(MultipartFile file) {
        if (file.isEmpty()) throw new BusinessException("File is empty");
        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new BusinessException("File type not allowed. Use JPEG, PNG, or WebP");
        if (file.getSize() > MAX_SIZE) throw new BusinessException("File size exceeds 5MB limit");

        try {
            Path dir = Paths.get(uploadDir);
            if (!Files.exists(dir)) Files.createDirectories(dir);

            String ext = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;
            Path dest = dir.resolve(filename);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            String url = "/api/files/" + filename;
            log.info("File uploaded: {}", filename);
            return Map.of("url", url, "filename", filename, "size", String.valueOf(file.getSize()));
        } catch (IOException e) {
            throw new BusinessException("Failed to store file: " + e.getMessage());
        }
    }

    public void deleteFile(String filename) {
        try {
            Path file = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", filename);
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".jpg";
    }
}
