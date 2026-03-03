package com.example.ecommerce.controller;

import com.example.ecommerce.dto.RegisterDTO;
import com.example.ecommerce.service.impl.CustomUserDetailsService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    private final CustomUserDetailsService userService;

    public RegisterController(CustomUserDetailsService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new RegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") RegisterDTO registerDTO) {

        userService.registerUser(registerDTO);

        return "redirect:/login";
    }
}