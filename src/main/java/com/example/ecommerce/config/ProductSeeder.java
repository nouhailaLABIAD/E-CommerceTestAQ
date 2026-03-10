package com.example.ecommerce.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;

@Component
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductSeeder(ProductRepository productRepository,
                         CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {

        if (productRepository.count() > 0) return;

        // Créer les catégories
        Category patisseries = new Category();
        patisseries.setNom("Pâtisseries");
        categoryRepository.save(patisseries);

        Category viennoiseries = new Category();
        viennoiseries.setNom("Viennoiseries");
        categoryRepository.save(viennoiseries);

        Category desserts = new Category();
        desserts.setNom("Desserts");
        categoryRepository.save(desserts);

        Category boissons = new Category();
        boissons.setNom("Boissons");
        categoryRepository.save(boissons);

        // Créer les produits
        Product p1 = new Product();
        p1.setNom("Croissant Beurre");
        p1.setDescription("Croissant artisanale au beurre frais");
        p1.setPrix(4.50);
        p1.setStock(50);
        p1.setCategory(viennoiseries);
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setNom("Tarte aux Fraises");
        p2.setDescription("Tarte garnie de fraises fraîches et crème pâtissière");
        p2.setPrix(12.00);
        p2.setStock(20);
        p2.setCategory(patisseries);
        productRepository.save(p2);

        Product p3 = new Product();
        p3.setNom("Macaron Assortis");
        p3.setDescription("Boîte de 12 macarons assortis (chocolat, pistache, fraise, citron)");
        p3.setPrix(18.00);
        p3.setStock(30);
        p3.setCategory(patisseries);
        productRepository.save(p3);

        Product p4 = new Product();
        p4.setNom("Éclair au Chocolat");
        p4.setDescription("Éclair garni de crème au chocolat et glaçage");
        p4.setPrix(5.50);
        p4.setStock(25);
        p4.setCategory(patisseries);
        productRepository.save(p4);

        Product p5 = new Product();
        p5.setNom("Mille-feuille");
        p5.setDescription("Pâte feuillée, crème pâtissière, glaçage");
        p5.setPrix(6.00);
        p5.setStock(15);
        p5.setCategory(patisseries);
        productRepository.save(p5);

        Product p6 = new Product();
        p6.setNom("Pain au Chocolat");
        p6.setDescription("Pain au chocolat artisanal");
        p6.setPrix(3.50);
        p6.setStock(40);
        p6.setCategory(viennoiseries);
        productRepository.save(p6);

        Product p7 = new Product();
        p7.setNom("Chocolat Chaud");
        p7.setDescription("Chocolat belge chaud avec chantilly");
        p7.setPrix(5.00);
        p7.setStock(100);
        p7.setCategory(boissons);
        productRepository.save(p7);

        Product p8 = new Product();
        p8.setNom("Crème Brûlée");
        p8.setDescription("Crème brûlée à la vanille avec sucre caramelisé");
        p8.setPrix(7.50);
        p8.setStock(18);
        p8.setCategory(desserts);
        productRepository.save(p8);
    }
}
