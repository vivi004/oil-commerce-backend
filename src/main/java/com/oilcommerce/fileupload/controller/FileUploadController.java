package com.oilcommerce.fileupload.controller;

import com.oilcommerce.common.ApiResponse; import com.oilcommerce.fileupload.service.FileUploadService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource; import org.springframework.core.io.UrlResource;
import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*; import org.springframework.web.multipart.MultipartFile;
import java.net.MalformedURLException; import java.nio.file.Paths; import java.util.Map;

@Tag(name="File Upload") @RestController @RequestMapping("/files") @RequiredArgsConstructor
public class FileUploadController {
    private final FileUploadService fileUploadService;
    @Value("${file.upload-dir:./uploads}") private String uploadDir;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("permitAll()")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Map<String,String>>> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success("File uploaded", fileUploadService.uploadFile(file)));
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            var path = Paths.get(uploadDir).resolve(filename);
            Resource res = new UrlResource(path.toUri());
            if (!res.exists()) return ResponseEntity.notFound().build();

            String contentType = null;
            try {
                contentType = java.nio.file.Files.probeContentType(path);
            } catch (java.io.IOException ignored) {}

            if (contentType == null) {
                if (filename.toLowerCase().endsWith(".png")) contentType = "image/png";
                else if (filename.toLowerCase().endsWith(".webp")) contentType = "image/webp";
                else if (filename.toLowerCase().endsWith(".svg")) contentType = "image/svg+xml";
                else contentType = "image/jpeg";
            }

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .contentType(MediaType.parseMediaType(contentType))
                .body(res);
        } catch (MalformedURLException e) { return ResponseEntity.badRequest().build(); }
    }

    @DeleteMapping("/{filename:.+}")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER','TENANT_ADMIN','SUPER_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String filename) {
        fileUploadService.deleteFile(filename); return ResponseEntity.ok(ApiResponse.success("File deleted",null));
    }
}
