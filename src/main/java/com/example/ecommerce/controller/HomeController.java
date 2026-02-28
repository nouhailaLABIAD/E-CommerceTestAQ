package com.example.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/admin/homeAdmin")
    public String homeAdmin() {
        return "homeAdmin"; // Thymeleaf page: homeAdmin.html
    }

    @GetMapping("/client/homeUser")
    public String homeUser() {
        return "homeUser"; // Thymeleaf page: homeUser.html
    }
}