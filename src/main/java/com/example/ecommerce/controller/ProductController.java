package com.example.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ecommerce.service.interfaces.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Voir catalogue
    @GetMapping
    public String getAllProducts(Model model) {
        model.addAttribute("products",
                productService.getAllAvailableProducts());
        return "products";
    }

    // Recherche
    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword, Model model) {
        model.addAttribute("products",
                productService.searchProducts(keyword));
        return "products";
    }

    // Détail produit
    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        model.addAttribute("product",
                productService.getProductById(id));
        return "product-detail";
    }
    
}