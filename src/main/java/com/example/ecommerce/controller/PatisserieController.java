package com.example.ecommerce.controller;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PatisserieController {

    private final UserRepository userRepository;

    public PatisserieController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/patisserie")
    public String homePatisserie(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        return "homePatisserie";
    }

    @GetMapping("/patisserie/add")
    public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity) {
        // REVIEW: Cette méthode est un stub pour la navigation ; la logique d'ajout au panier
        // est gérée par CartController. Redirection vers la page d'accueil avec paramètre.
        return "redirect:/patisserie?added=" + productId;
    }
}

