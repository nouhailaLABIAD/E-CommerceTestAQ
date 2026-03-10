package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.ecommerce.entity.Category;

/**
 * Repository pour les catégories de produits.
 * 
 * @author Equipe E-Commerce
 * @version 1.0
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Récupère toutes les catégories actives.
     * 
     * @return Liste des catégories
     */
    @Query("SELECT c FROM Category c WHERE c.nom IS NOT NULL")
    List<Category> findAllActive();
}

