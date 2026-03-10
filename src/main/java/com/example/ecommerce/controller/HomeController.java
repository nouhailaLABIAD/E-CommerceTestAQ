package com.example.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/admin/homeAdmin")
    public String homeAdmin() {
        return "homeAdmin"; // Thymeleaf page: homeAdmin.html
    }
    
    // Note: /client/homePatisserie est géré par ClientProductController
}
