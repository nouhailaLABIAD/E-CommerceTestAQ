package com.example.ecommerce.controller;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur pour la gestion du panier.
 * Gère l'ajout, la modification et la suppression de produits dans le panier.
 * 
 * @author Membre 3 - Phase 4
 * @version 1.0
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    /**
     * Affiche le panier de l'utilisateur connecté.
     * 
     * @param model Le modèle pour la vue
     * @return La vue du panier
     */
    @GetMapping
    public String showCart(Model model) {
        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login?redirect=/cart";
        }
        
        model.addAttribute("cart", cartService.getCartByUser(user));
        model.addAttribute("total", cartService.getCartTotal(user));
        model.addAttribute("itemCount", cartService.getCartItemCount(user));
        model.addAttribute("user", user);
        
        return "cart";
    }

    /**
     * Ajoute un produit au panier.
     * 
     * @param productId L'ID du produit
     * @param quantity La quantité
     * @param redirectAttributes Attributs pour les messages flash
     * @return Redirection vers le panier ou la page précédente
     */
    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                           @RequestParam(defaultValue = "1") int quantity,
                           RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();
        
        if (user == null) {
            return "redirect:/login?redirect=/products";
        }
        
        try {
            cartService.addProduct(user, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Produit ajouté au panier!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cart";
    }

    /**
     * Met à jour la quantité d'un produit dans le panier.
     * 
     * @param productId L'ID du produit
     * @param quantity La nouvelle quantité
     * @param redirectAttributes Attributs pour les messages flash
     * @return Redirection vers le panier
     */
    @PostMapping("/update/{productId}")
    public String updateQuantity(@PathVariable Long productId,
                                 @RequestParam int quantity,
                                 RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();
        
        if (user == null) {
            return "redirect:/login?redirect=/cart";
        }
        
        try {
            if (quantity <= 0) {
                cartService.removeProduct(user, productId);
                redirectAttributes.addFlashAttribute("success", "Produit supprimé du panier");
            } else {
                cartService.updateQuantity(user, productId, quantity);
                redirectAttributes.addFlashAttribute("success", "Quantité mise à jour");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cart";
    }

    /**
     * Supprime un produit du panier.
     * 
     * @param productId L'ID du produit à supprimer
     * @param redirectAttributes Attributs pour les messages flash
     * @return Redirection vers le panier
     */
    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId,
                                 RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();
        
        if (user == null) {
            return "redirect:/login?redirect=/cart";
        }
        
        try {
            cartService.removeProduct(user, productId);
            redirectAttributes.addFlashAttribute("success", "Produit supprimé du panier");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cart";
    }

    /**
     * Vide le panier de l'utilisateur.
     * 
     * @param redirectAttributes Attributs pour les messages flash
     * @return Redirection vers le panier
     */
    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();
        
        if (user == null) {
            return "redirect:/login?redirect=/cart";
        }
        
        try {
            cartService.clearCart(user);
            redirectAttributes.addFlashAttribute("success", "Panier vidé");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cart";
    }

    /**
     * Récupère l'utilisateur actuellement connecté.
     * 
     * @return L'utilisateur ou null si non connecté
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() 
            && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            return userRepository.findByEmail(email).orElse(null);
        }
        
        return null;
    }
}

