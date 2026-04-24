package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Sql(scripts = "/test-data/categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_returnsAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        assertFalse(categories.isEmpty());
        assertTrue(categories.size() >= 3);
    }

    @Test
    void findById_existing_returnsCategory() {
        Category category = new Category();
        category.setNom("Test Category");
        Category saved = categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Test Category", found.get().getNom());
    }

    @Test
    void findById_nonExisting_returnsEmpty() {
        Optional<Category> found = categoryRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void save_createsNewCategory() {
        Category category = new Category();
        category.setNom("New Category");

        Category saved = categoryRepository.save(category);

        assertNotNull(saved.getId());
        assertEquals("New Category", saved.getNom());
    }

    @Test
    void deleteById_removesCategory() {
        Category category = new Category();
        category.setNom("To Delete");
        Category saved = categoryRepository.save(category);

        categoryRepository.deleteById(saved.getId());

        Optional<Category> deleted = categoryRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }
}

