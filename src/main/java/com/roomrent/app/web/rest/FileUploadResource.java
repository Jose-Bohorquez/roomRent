package com.roomrent.app.web.rest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for handling media file uploads.
 * Files are stored on the server filesystem and served at /uploads/*.
 */
@RestController
@RequestMapping("/api/uploads")
public class FileUploadResource {

    private static final Logger LOG = LoggerFactory.getLogger(FileUploadResource.class);

    private static final long MAX_SIZE_BYTES = 20L * 1024 * 1024; // 20 MB

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
        "image/png",
        "image/jpeg",
        "image/webp",
        "image/heic",
        "image/heif"
    );

    private static final Map<String, String> MIME_TO_EXT = Map.of(
        "image/png", ".png",
        "image/jpeg", ".jpg",
        "image/webp", ".webp",
        "image/heic", ".heic",
        "image/heif", ".heif"
    );

    @Value("${roomrent.upload.path:/app/uploads}")
    private String uploadPath;

    @Value("${roomrent.upload.base-url:}")
    private String uploadBaseUrl;

    /**
     * {@code POST /api/uploads/multimedia} : Upload a media file.
     *
     * @param file the multipart file to upload.
     * @return URL where the file can be accessed.
     */
    @PostMapping(value = "/multimedia", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> uploadMultimedia(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El archivo está vacío"));
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            return ResponseEntity.badRequest().body(Map.of("error", "El archivo supera el límite de 20 MB"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "Tipo de archivo no permitido. Use PNG, JPG, WEBP o HEIC")
            );
        }

        String extension = MIME_TO_EXT.getOrDefault(contentType.toLowerCase(), ".jpg");
        String filename = UUID.randomUUID() + extension;

        Path uploadDir = Paths.get(uploadPath);
        Files.createDirectories(uploadDir);

        Path destination = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = (uploadBaseUrl.isBlank() ? "" : uploadBaseUrl) + "/uploads/" + filename;
        LOG.info("Archivo subido: {} ({} bytes)", filename, file.getSize());

        return ResponseEntity.created(URI.create(fileUrl)).body(Map.of("url", fileUrl, "filename", filename));
    }

    /**
     * {@code DELETE /api/uploads/multimedia/{filename}} : Delete an uploaded file.
     *
     * @param filename the filename to delete.
     * @return 204 on success, 404 if not found.
     */
    @DeleteMapping("/multimedia/{filename}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ARRENDADOR')")
    public ResponseEntity<Void> deleteMultimedia(@PathVariable String filename) throws IOException {
        if (filename.contains("..") || filename.contains("/")) {
            return ResponseEntity.badRequest().build();
        }

        Path file = Paths.get(uploadPath).resolve(filename);
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        Files.delete(file);
        LOG.info("Archivo eliminado: {}", filename);
        return ResponseEntity.noContent().build();
    }
}
