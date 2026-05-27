package com.gestionganadera.backend.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

public class FileUploadUtil {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx"
    );

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir).normalize().toAbsolutePath();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Sanitize fileName: prevent path traversal
        String sanitizedFileName = Paths.get(fileName).getFileName().toString();

        // Validate file extension
        String extension = getExtension(sanitizedFileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + extension);
        }

        Path targetPath = uploadPath.resolve(sanitizedFileName).normalize();

        // Ensure the resolved path is still within the upload directory
        if (!targetPath.startsWith(uploadPath)) {
            throw new SecurityException("Intento de path traversal detectado");
        }

        try (var inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return fileName.substring(lastDot + 1);
    }
}
