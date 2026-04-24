package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByDeletedFalse_returnsOnlyNonDeleted() {
        Product p1 = new Product();
        p1.setNom("Active");
        p1.setDeleted(false);
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setNom("Deleted");
        p2.setDeleted(true);
        productRepository.save(p2);

        List<Product> result = productRepository.findByDeletedFalse();

        assertTrue(result.stream().allMatch(p -> !p.isDeleted()));
        assertTrue(result.stream().anyMatch(p -> p.getNom().equals("Active")));
    }

    @Test
    void findByNomContainingIgnoreCaseAndDeletedFalse_returnsMatching() {
        Product p = new Product();
        p.setNom("Chocolate Cake");
        p.setDeleted(false);
        productRepository.save(p);

        List<Product> result = productRepository.findByNomContainingIgnoreCaseAndDeletedFalse("cake");

        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getNom().toLowerCase().contains("cake"));
    }

    @Test
    void save_createsProduct() {
        Product product = new Product();
        product.setNom("New Product");
        product.setPrix(10.0);
        product.setStock(5);
        product.setDeleted(false);

        Product saved = productRepository.save(product);

        assertNotNull(saved.getId());
        assertEquals("New Product", saved.getNom());
    }

    @Test
    void findById_existing_returnsProduct() {
        Product product = new Product();
        product.setNom("Find Me");
        product.setDeleted(false);
        Product saved = productRepository.save(product);

        Optional<Product> found = productRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Find Me", found.get().getNom());
    }

    @Test
    void deleteById_removesProduct() {
        Product product = new Product();
        product.setNom("To Delete");
        product.setDeleted(false);
        Product saved = productRepository.save(product);

        productRepository.deleteById(saved.getId());

        Optional<Product> deleted = productRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }
}

