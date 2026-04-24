package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getProductById_success() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertEquals(product, result);
    }

    @Test
    void getProductById_notFound_throwsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(999L));
    }

    @Test
    void createProduct_success() {
        Product product = new Product();
        product.setNom("Test Product");
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.createProduct(product);

        assertEquals(product, result);
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_withNullCategory_keepsExistingCategory() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);

        Product existing = new Product();
        existing.setId(1L);
        existing.setCategory(existingCategory);
        existing.setImageUrl("/old.jpg");

        Product update = new Product();
        update.setNom("Updated");
        update.setPrix(20.0);
        update.setStock(5);
        update.setDescription("Desc");
        // category null -> should keep existing

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        Product result = productService.updateProduct(1L, update);

        assertEquals("Updated", result.getNom());
        assertEquals(existingCategory, result.getCategory());
    }

    @Test
    void updateProduct_withNullImage_keepsExistingImage() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setImageUrl("/keep.jpg");

        Product update = new Product();
        update.setNom("Updated");
        update.setImageUrl(null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        Product result = productService.updateProduct(1L, update);

        assertEquals("/keep.jpg", result.getImageUrl());
    }

    @Test
    void updateProduct_withEmptyImage_keepsExistingImage() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setImageUrl("/keep.jpg");

        Product update = new Product();
        update.setNom("Updated");
        update.setImageUrl("");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        Product result = productService.updateProduct(1L, update);

        assertEquals("/keep.jpg", result.getImageUrl());
    }
}

