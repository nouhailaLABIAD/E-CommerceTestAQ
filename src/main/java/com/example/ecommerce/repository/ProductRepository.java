package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Catalogue public
    List<Product> findByDeletedFalse();

    // Recherche
    List<Product> findByNomContainingIgnoreCaseAndDeletedFalse(String keyword);
}