package com.gestionganadera.backend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUploadUtilTest {

    @TempDir
    Path tempDir;

    private FileUploadUtil fileUploadUtil;

    @BeforeEach
    void setUp() {
        fileUploadUtil = new FileUploadUtil();
        // Use reflection to set uploadDir and maxFileSize since @Value won't be injected in tests
        try {
            var uploadDirField = FileUploadUtil.class.getDeclaredField("uploadDir");
            uploadDirField.setAccessible(true);
            uploadDirField.set(fileUploadUtil, tempDir.toString() + "/");

            var maxFileSizeField = FileUploadUtil.class.getDeclaredField("maxFileSize");
            maxFileSizeField.setAccessible(true);
            maxFileSizeField.set(fileUploadUtil, 5 * 1024 * 1024L); // 5MB
        } catch (Exception e) {
            throw new RuntimeException("Failed to set test fields", e);
        }
        fileUploadUtil.init();
    }

    @Test
    void saveFile_allowedExtension_savesSuccessfully() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "test-image-content".getBytes());

        fileUploadUtil.saveFile(file);

        Path savedPath = tempDir.resolve("photo.jpg");
        assertTrue(Files.exists(savedPath));
        assertEquals("test-image-content", Files.readString(savedPath));
    }

    @Test
    void saveFile_createsDirectoryIfNotExists() throws IOException {
        Path nestedDir = tempDir.resolve("subdir/uploads");
        try {
            var uploadDirField = FileUploadUtil.class.getDeclaredField("uploadDir");
            uploadDirField.setAccessible(true);
            uploadDirField.set(fileUploadUtil, nestedDir.toString() + "/");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set uploadDir", e);
        }
        fileUploadUtil.init();

        MultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "pdf-content".getBytes());

        fileUploadUtil.saveFile(file);

        assertTrue(Files.exists(nestedDir.resolve("doc.pdf")));
    }

    @Test
    void saveFile_disallowedExtension_throwsException() {
        MultipartFile file = new MockMultipartFile(
                "file", "script.exe", "application/x-msdownload", "bad".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileUploadUtil.saveFile(file));

        assertTrue(exception.getMessage().contains("exe"));
    }

    @Test
    void saveFile_pathTraversal_sanitizesFilename() throws IOException {
        // The getFileName() sanitization strips directory components
        MultipartFile file = new MockMultipartFile(
                "file", "../../etc/passwd.jpg", "image/jpeg", "hack".getBytes());

        fileUploadUtil.saveFile(file);

        // File should be saved with just the base name (no path traversal)
        assertTrue(Files.exists(tempDir.resolve("passwd.jpg")));
        // Should NOT create files in parent directories
        assertFalse(Files.exists(tempDir.resolve("..").resolve("etc").resolve("passwd.jpg").normalize()));
    }

    @Test
    void saveFile_allowedExtensions_acceptsAll() {
        String[] allowed = {"jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx"};

        for (String ext : allowed) {
            String fileName = "file." + ext;
            MultipartFile file = new MockMultipartFile(
                    "file", fileName, "application/octet-stream", "content".getBytes());

            assertDoesNotThrow(() ->
                            fileUploadUtil.saveFile(file),
                    "Extension ." + ext + " should be allowed");
        }
    }

    @Test
    void saveFile_noExtension_throwsException() {
        MultipartFile file = new MockMultipartFile(
                "file", "README", "text/plain", "content".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileUploadUtil.saveFile(file));

        assertTrue(exception.getMessage().contains("no permitido"));
    }

    @Test
    void saveFile_emptyFile_throwsException() {
        MultipartFile file = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileUploadUtil.saveFile(file));

        assertTrue(exception.getMessage().contains("vacío"));
    }
}
