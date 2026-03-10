package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.service.FileStorageService;
import com.example.ecommerce.service.interfaces.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur admin pour la gestion des catégories.
 * Gère le CRUD catégorie avec upload d'image.
 */
@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;

    public CategoryController(CategoryService categoryService,
                               FileStorageService fileStorageService) {
        this.categoryService = categoryService;
        this.fileStorageService = fileStorageService;
    }

    // ── Liste ────────────────────────────────────────────────────────────────
    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin-categories";
    }

    // ── Formulaire création ──────────────────────────────────────────────────
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "category-form";
    }

    // ── Créer catégorie ──────────────────────────────────────────────────────
    @PostMapping
    public String createCategory(
            @ModelAttribute Category category,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
            throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.saveImage(imageFile, "categories");
            category.setImageUrl(imageUrl);
        }

        categoryService.createCategory(category);
        return "redirect:/admin/categories";
    }

    // ── Formulaire édition ───────────────────────────────────────────────────
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id);
        model.addAttribute("category", category);
        return "category-form";
    }

    // ── Mettre à jour catégorie ──────────────────────────────────────────────
    @PostMapping("/edit/{id}")
    public String updateCategory(
            @PathVariable Long id,
            @ModelAttribute Category category,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "existingImageUrl", required = false) String existingImageUrl)
            throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                fileStorageService.deleteImage(existingImageUrl);
            }
            String imageUrl = fileStorageService.saveImage(imageFile, "categories");
            category.setImageUrl(imageUrl);
        } else {
            category.setImageUrl(existingImageUrl);
        }

        categoryService.updateCategory(id, category);
        return "redirect:/admin/categories";
    }

    // ── Supprimer catégorie ──────────────────────────────────────────────────
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }
}
