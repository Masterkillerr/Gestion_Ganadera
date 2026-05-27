package com.gestionganadera.backend.util;

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

    @Test
    void saveFile_allowedExtension_savesSuccessfully() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "test-image-content".getBytes());

        FileUploadUtil.saveFile(tempDir.toString(), "photo.jpg", file);

        Path savedPath = tempDir.resolve("photo.jpg");
        assertTrue(Files.exists(savedPath));
        assertEquals("test-image-content", Files.readString(savedPath));
    }

    @Test
    void saveFile_createsDirectoryIfNotExists() throws IOException {
        Path nestedDir = tempDir.resolve("subdir/uploads");
        MultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "pdf-content".getBytes());

        FileUploadUtil.saveFile(nestedDir.toString(), "doc.pdf", file);

        assertTrue(Files.exists(nestedDir.resolve("doc.pdf")));
    }

    @Test
    void saveFile_disallowedExtension_throwsException() {
        MultipartFile file = new MockMultipartFile(
                "file", "script.exe", "application/x-msdownload", "bad".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FileUploadUtil.saveFile(tempDir.toString(), "script.exe", file));

        assertTrue(exception.getMessage().contains("exe"));
    }

    @Test
    void saveFile_pathTraversal_sanitizesFilename() throws IOException {
        // The getFileName() sanitization strips directory components
        MultipartFile file = new MockMultipartFile(
                "file", "../../etc/passwd.jpg", "image/jpeg", "hack".getBytes());

        FileUploadUtil.saveFile(tempDir.toString(), "../../etc/passwd.jpg", file);

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
                    FileUploadUtil.saveFile(tempDir.toString(), fileName, file),
                    "Extension ." + ext + " should be allowed");
        }
    }

    @Test
    void saveFile_noExtension_throwsException() {
        MultipartFile file = new MockMultipartFile(
                "file", "README", "text/plain", "content".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FileUploadUtil.saveFile(tempDir.toString(), "README", file));

        assertTrue(exception.getMessage().contains("no permitido"));
    }
}
