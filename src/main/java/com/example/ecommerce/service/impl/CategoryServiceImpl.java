package com.example.ecommerce.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.interfaces.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        Category existing = getCategoryById(id);

        // Mettre à jour le nom
        existing.setNom(category.getNom());

        // Mettre à jour l'image uniquement si une nouvelle valeur est fournie
        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            existing.setImageUrl(category.getImageUrl());
        }
        // Sinon on garde l'imageUrl existante en base — rien à faire

        return categoryRepository.save(existing);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}