package com.example.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // page login.html Thymeleaf
    }

    @GetMapping("/")
    public String homePage() {
        return "homePatisserie"; // page index.html
    }
}