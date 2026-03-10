package com.example.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration MVC pour servir les fichiers uploadés.
 * 
 * Les images sont stockées dans : src/main/resources/static/uploads/
 * et accessibles via l'URL :     /uploads/...
 * 
 * Spring Boot sert automatiquement le dossier static/,
 * donc cette config n'est nécessaire que si vous déplacez
 * les uploads en dehors de src/main/resources/static.
 * 
 * Si vous utilisez le chemin par défaut (static/uploads),
 * ce fichier est optionnel mais recommandé pour la clarté.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir les images depuis src/main/resources/static/uploads/
        // URL publique : /uploads/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/");
    }
}
