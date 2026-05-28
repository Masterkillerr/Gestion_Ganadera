package com.gestionganadera.backend.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

@Component
public class FileUploadUtil {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx"
    );

    @Value("${app.upload.dir:../uploads/}")
    @Getter
    private String uploadDir;

    @Value("${app.upload.max-size:5242880}")
    @Getter
    private long maxFileSize;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads: " + uploadDir, e);
        }
    }

    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    "El archivo excede el tamaño máximo permitido de " + (maxFileSize / 1024 / 1024) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo no es válido");
        }

        String extension = getExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + extension);
        }

        String sanitizedFilename = sanitizeFilename(originalFilename);
        Path targetPath = Paths.get(uploadDir, sanitizedFilename).normalize();

        if (!targetPath.startsWith(Paths.get(uploadDir).normalize())) {
            throw new IllegalArgumentException("Intento de path traversal detectado");
        }

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return sanitizedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + originalFilename, e);
        }
    }

    private String sanitizeFilename(String filename) {
        return Paths.get(filename).getFileName().toString();
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot + 1);
    }
}
