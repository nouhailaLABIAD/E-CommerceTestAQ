package com.example.ecommerce.service.impl;

import com.example.ecommerce.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    private void setUploadDir() {
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString() + "/");
    }

    @Test
    void saveImage_nullFile_returnsNull() throws IOException {
        String result = fileStorageService.saveImage(null, "products");
        assertNull(result);
    }

    @Test
    void saveImage_emptyFile_returnsNull() throws IOException {
        MockMultipartFile emptyFile = new MockMultipartFile("imageFile", "", "image/jpeg", new byte[0]);
        String result = fileStorageService.saveImage(emptyFile, "products");
        assertNull(result);
    }

    @Test
    void saveImage_invalidContentType_throwsException() throws IOException {
        MockMultipartFile textFile = new MockMultipartFile("imageFile", "test.txt", "text/plain", "test".getBytes());
        assertThrows(IllegalArgumentException.class, () -> fileStorageService.saveImage(textFile, "products"));
    }

    @Test
    void saveImage_unauthorizedExtension_throwsException() {
        setUploadDir();
        MockMultipartFile file = new MockMultipartFile("imageFile", "test.exe", "image/jpeg", "test".getBytes());
        assertThrows(IllegalArgumentException.class, () -> fileStorageService.saveImage(file, "products"));
    }

    @Test
    void saveImage_nullContentType_throwsException() {
        setUploadDir();
        MockMultipartFile file = new MockMultipartFile("imageFile", "test.jpg", null, "test".getBytes());
        assertThrows(IllegalArgumentException.class, () -> fileStorageService.saveImage(file, "products"));
    }

    @Test
    void saveImage_validFile_success() throws IOException {
        setUploadDir();
        MockMultipartFile file = new MockMultipartFile("imageFile", "test.jpg", "image/jpeg", "test".getBytes());

        String result = fileStorageService.saveImage(file, "products");

        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/products/"));
        assertTrue(result.endsWith(".jpg"));

        Path savedFile = tempDir.resolve("products").resolve(result.substring(result.lastIndexOf("/") + 1));
        assertTrue(Files.exists(savedFile));
    }

    @Test
    void saveImage_validWebp_success() throws IOException {
        setUploadDir();
        MockMultipartFile file = new MockMultipartFile("imageFile", "test.webp", "image/webp", "test".getBytes());

        String result = fileStorageService.saveImage(file, "categories");

        assertNotNull(result);
        assertTrue(result.endsWith(".webp"));
    }

    @Test
    void deleteImage_nullUrl_noException() {
        assertDoesNotThrow(() -> fileStorageService.deleteImage(null));
    }

    @Test
    void deleteImage_emptyUrl_noException() {
        assertDoesNotThrow(() -> fileStorageService.deleteImage(""));
    }

    @Test
    void deleteImage_existingFile_success() throws IOException {
        setUploadDir();
        Path productsDir = tempDir.resolve("uploads").resolve("products");
        Files.createDirectories(productsDir);
        Path filePath = productsDir.resolve("test-file.jpg");
        Files.write(filePath, "test content".getBytes());

        assertTrue(Files.exists(filePath));

        fileStorageService.deleteImage("/uploads/products/test-file.jpg");

        assertFalse(Files.exists(filePath));
    }

    @Test
    void deleteImage_pathTraversal_doesNothing() throws IOException {
        setUploadDir();
        Path outsideFile = tempDir.getParent().resolve("outside.txt");
        Files.write(outsideFile, "secret".getBytes());

        assertTrue(Files.exists(outsideFile));

        fileStorageService.deleteImage("/uploads/../outside.txt");

        assertTrue(Files.exists(outsideFile));
    }

    @Test
    void deleteImage_nonExistentFile_noException() {
        setUploadDir();
        assertDoesNotThrow(() -> fileStorageService.deleteImage("/uploads/products/nonexistent.jpg"));
    }

    @Test
    void deleteImage_ioException_logsWarning() {
        setUploadDir();
        assertDoesNotThrow(() -> fileStorageService.deleteImage("/uploads/products/"));
    }
}
