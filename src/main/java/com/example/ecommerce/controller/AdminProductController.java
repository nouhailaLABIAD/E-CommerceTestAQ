package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.FileStorageService;
import com.example.ecommerce.service.interfaces.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public AdminProductController(ProductService productService,
                                  CategoryRepository categoryRepository,
                                  FileStorageService fileStorageService) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    // ── Liste ────────────────────────────────────────────────────────────────
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllAvailableProducts());
        return "admin-products";
    }

    // ── Formulaire création ──────────────────────────────────────────────────
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "product-form";
    }

    // ── Créer produit ────────────────────────────────────────────────────────
    @PostMapping
    public String createProduct(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") double prix,
            @RequestParam("stock") int stock,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) throws IOException {

        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            model.addAttribute("product", new Product());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("error", "Catégorie introuvable.");
            return "product-form";
        }

        Product product = new Product();
        product.setNom(nom);
        product.setDescription(description);
        product.setPrix(prix);
        product.setStock(stock);
        product.setCategory(category);

        if (imageFile != null && !imageFile.isEmpty()) {
            product.setImageUrl(fileStorageService.saveImage(imageFile, "products"));
        }

        productService.createProduct(product);
        return "redirect:/admin/products";
    }

    // ── Formulaire édition ───────────────────────────────────────────────────
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "product-form";
    }

    // ── Mettre à jour produit ────────────────────────────────────────────────
    @PostMapping("/edit/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") double prix,
            @RequestParam("stock") int stock,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "existingImageUrl", required = false) String existingImageUrl,
            Model model) throws IOException {

        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("error", "Catégorie introuvable.");
            return "product-form";
        }

        // Construire le produit mis à jour
        Product updatedProduct = new Product();
        updatedProduct.setNom(nom);
        updatedProduct.setDescription(description);
        updatedProduct.setPrix(prix);
        updatedProduct.setStock(stock);
        updatedProduct.setCategory(category);

        // Image : nouvelle ou conserver l'ancienne
        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                fileStorageService.deleteImage(existingImageUrl);
            }
            updatedProduct.setImageUrl(fileStorageService.saveImage(imageFile, "products"));
        } else {
            updatedProduct.setImageUrl(existingImageUrl);
        }

        productService.updateProduct(id, updatedProduct);
        return "redirect:/admin/products";
    }

    // ── Supprimer produit (soft delete) ──────────────────────────────────────
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.softDeleteProduct(id);
        return "redirect:/admin/products";
    }
}
