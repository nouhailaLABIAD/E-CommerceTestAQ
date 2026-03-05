package com.example.ecommerce.controller;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PatisserieController {

    @Autowired
    private UserRepository userRepository;

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

    @PostMapping("/patisserie/add")
    public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity) {
        // Cette methode sera completee une fois ProductService fini par Membre 1
        // Pour l'instant, redirection vers la page
        return "redirect:/patisserie?added=" + productId;
    }
}

