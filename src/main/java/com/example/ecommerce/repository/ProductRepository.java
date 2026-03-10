package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Category;

/**
 * Repository pour les produits.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Récupère tous les produits non supprimés.
     * Inclut aussi les produits où deleted est NULL (legacy).
     */
    @Query("SELECT p FROM Product p WHERE p.deleted = false OR p.deleted IS NULL")
    List<Product> findByDeletedFalse();

    /**
     * Recherche les produits par nom (insensible à la casse).
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) AND (p.deleted = false OR p.deleted IS NULL)")
    List<Product> findByNomContainingIgnoreCaseAndDeletedFalse(@Param("keyword") String keyword);

    /**
     * Récupère les produits par catégorie.
     */
    @Query("SELECT p FROM Product p WHERE p.category = :category AND (p.deleted = false OR p.deleted IS NULL)")
    List<Product> findByCategoryAndDeletedFalse(@Param("category") Category category);

    /**
     * Récupère les produits par ID de catégorie.
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND (p.deleted = false OR p.deleted IS NULL)")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Récupère les produits en stock.
     */
    @Query("SELECT p FROM Product p WHERE (p.deleted = false OR p.deleted IS NULL) AND p.stock > 0")
    List<Product> findAvailableInStock();
}
