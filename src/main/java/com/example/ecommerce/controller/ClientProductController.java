package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour les pages client (boutique).
 * Gère l'affichage des produits, les catégories et la recherche.
 * 
 * @author Membre 3 - Phase 4
 * @version 1.0
 */
@Controller
@RequestMapping("/client")
public class ClientProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartService cartService;
    private final UserRepository userRepository;

    public ClientProductController(ProductRepository productRepository,
                                  CategoryRepository categoryRepository,
                                  CartService cartService,
                                  UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    /**
     * Page d'accueil de la boutique client.
     * 
     * @param model Le modèle pour la vue
     * @return La vue homePatisserie
     */
    @GetMapping("/homePatisserie")
    public String homePatisserie(Model model) {
        addUserToModel(model);
        
        // Charger les catégories et produits en vedette
        List<Category> categories = categoryRepository.findAll();
        List<Product> featuredProducts = productRepository.findByDeletedFalse();
        
        model.addAttribute("categories", categories);
        model.addAttribute("featuredProducts", featuredProducts != null && featuredProducts.size() > 8 
            ? featuredProducts.subList(0, 8) 
            : featuredProducts);
        
        return "homePatisserie";
    }

    /**
     * Page collections - affiche les produits par catégorie.
     * 
     * @param categoryId ID de la catégorie (optionnel)
     * @param model Le modèle pour la vue
     * @return La vue products
     */
    @GetMapping("/products")
    public String products(@RequestParam(required = false) Long categoryId,
                         Model model) {
        addUserToModel(model);
        
        List<Category> categories = categoryRepository.findAll();
        List<Product> products;
        
        if (categoryId != null) {
            // Produits d'une catégorie spécifique
            products = productRepository.findAll().stream()
                .filter(p -> !p.isDeleted() && p.getCategory() != null 
                    && p.getCategory().getId().equals(categoryId))
                .toList();
            model.addAttribute("selectedCategory", categoryId);
        } else {
            // Tous les produits
            products = productRepository.findByDeletedFalse();
        }
        
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        
        return "client-products";
    }

    /**
     * Recherche de produits.
     * 
     * @param keyword Le mot-clé de recherche
     * @param model Le modèle pour la vue
     * @return La vue products avec les résultats
     */
    @GetMapping("/products/search")
    public String searchProducts(@RequestParam String keyword, Model model) {
        addUserToModel(model);
        
        List<Category> categories = categoryRepository.findAll();
        List<Product> products = productRepository
                .findByNomContainingIgnoreCaseAndDeletedFalse(keyword);
        
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("searchKeyword", keyword);
        
        return "client-products";
    }

    /**
     * Détail d'un produit.
     * 
     * @param id ID du produit
     * @param model Le modèle pour la vue
     * @return La vue product-detail
     */
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        addUserToModel(model);
        
        Optional<Product> productOpt = productRepository.findById(id);
        
        if (productOpt.isEmpty() || productOpt.get().isDeleted()) {
            return "redirect:/client/products";
        }
        
        model.addAttribute("product", productOpt.get());
        
        // Produits similaires dans la même catégorie
        if (productOpt.get().getCategory() != null) {
            List<Product> similarProducts = productRepository.findByDeletedFalse().stream()
                .filter(p -> p.getCategory() != null 
                    && p.getCategory().getId().equals(productOpt.get().getCategory().getId())
                    && !p.getId().equals(id))
                .limit(4)
                .toList();
            model.addAttribute("similarProducts", similarProducts);
        }
        
        return "product-detail";
    }

    /**
     * Ajoute l'utilisateur courant et les infos du panier au modèle.
     * 
     * @param model Le modèle
     */
    private void addUserToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() 
            && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("user", user);
                model.addAttribute("cartItemCount", cartService.getCartItemCount(user));
            }
        } else {
            model.addAttribute("cartItemCount", 0);
        }
    }
}

