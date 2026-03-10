package com.example.ecommerce.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    // Chemin de l'image stockée (ex: /uploads/categories/photo.jpg)
    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Product> products;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Set<Product> getProducts() { return products; }
    public void setProducts(Set<Product> products) { this.products = products; }
}
