package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register"; // register.html Thymeleaf
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("errorEmail", "Cet email est déjà utilisé.");
            return "register";
        }

        // Encoder le mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Rôle par défaut : CLIENT
        user.setRole(Role.CLIENT);

        // Sauvegarder
        userRepository.save(user);

        return "redirect:/login?registered=true";
    }
}