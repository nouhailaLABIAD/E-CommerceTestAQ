package com.example.ecommerce.service.interfaces;

import java.util.List;

import com.example.ecommerce.entity.Category;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
}