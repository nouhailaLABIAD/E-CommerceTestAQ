package com.example.ecommerce.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.interfaces.CategoryService;
import com.example.ecommerce.service.interfaces.ProductService;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public AdminProductController(ProductService productService,
                                  CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // 🔹 Liste admin
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products",
                productService.getAllAvailableProducts());
        return "admin-products";
    }

    // 🔹 Form création
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "product-form";
    }

    // 🔹 SAVE AVEC VALIDATION
    @PostMapping
    public String saveProduct(@Valid @ModelAttribute Product product,
                              BindingResult result,
                              Model model) {

        // 🔴 Si erreur validation
        if (result.hasErrors()) {
            model.addAttribute("categories",
                    categoryService.getAllCategories());
            return "product-form";
        }

        // 🔹 Recharger la vraie catégorie depuis la base
        Category category =
                categoryService.getCategoryById(
                        product.getCategory().getId());

        product.setCategory(category);

        productService.createProduct(product);

        return "redirect:/admin/products";
    }

    // 🔹 Form update
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product",
                productService.getProductById(id));
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "product-form";
    }

    // 🔹 UPDATE AVEC VALIDATION
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute Product product,
                                BindingResult result,
                                Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories",
                    categoryService.getAllCategories());
            return "product-form";
        }

        Category category =
                categoryService.getCategoryById(
                        product.getCategory().getId());

        product.setCategory(category);

        productService.updateProduct(id, product);

        return "redirect:/admin/products";
    }

    // 🔹 Soft delete
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.softDeleteProduct(id);
        return "redirect:/admin/products";
    }
}