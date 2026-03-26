package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.interfaces.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setNom("Pâtisserie");
        testCategory.setImageUrl("/images/patisserie.jpg");
    }

    @Test
    void getAllCategories() {
        // Arrange
        List<Category> expectedCategories = List.of(testCategory);
        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertEquals(expectedCategories, result);
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById_success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        Category result = categoryService.getCategoryById(1L);

        // Assert
        assertEquals(testCategory, result);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_notFound_throwsException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> categoryService.getCategoryById(999L));
        verify(categoryRepository).findById(999L);
    }

    @Test
    void createCategory() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Category result = categoryService.createCategory(testCategory);

        // Assert
        assertEquals(testCategory, result);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void updateCategory_success() {
        // Arrange
        Category updatedCategory = new Category();
        updatedCategory.setNom("Updated Pâtisserie");
        updatedCategory.setImageUrl("/new-image.jpg");

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setNom("Old");
        existingCategory.setImageUrl("/old.jpg");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        // Act
        Category result = categoryService.updateCategory(1L, updatedCategory);

        // Assert
        assertEquals("Updated Pâtisserie", result.getNom());
        assertEquals("/new-image.jpg", result.getImageUrl());
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void updateCategory_noImageUpdate() {
        // Arrange
        Category updateNoImage = new Category();
        updateNoImage.setNom("Updated Name");

        Category existing = new Category();
        existing.setId(1L);
        existing.setImageUrl("/keep-this.jpg");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        // Act
        Category result = categoryService.updateCategory(1L, updateNoImage);

        // Assert: image kept
        assertEquals("/keep-this.jpg", result.getImageUrl());
    }

    @Test
    void deleteCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).delete(testCategory);
    }
}
