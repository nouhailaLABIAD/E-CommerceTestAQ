package com.example.ecommerce.service.impl;

import com.example.ecommerce.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

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
    void deleteImage_nullUrl_noException() {
        assertDoesNotThrow(() -> fileStorageService.deleteImage(null));
    }

    @Test
    void deleteImage_emptyUrl_noException() {
        assertDoesNotThrow(() -> fileStorageService.deleteImage(""));
    }
}

