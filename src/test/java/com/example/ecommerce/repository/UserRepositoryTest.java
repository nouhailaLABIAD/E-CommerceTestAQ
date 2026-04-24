package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_existing_returnsUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("Password123!");
        user.setNom("Test User");
        user.setRole(Role.CLIENT);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getNom());
    }

    @Test
    void findByEmail_nonExisting_returnsEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        assertFalse(found.isPresent());
    }

    @Test
    void save_createsUser() {
        User user = new User();
        user.setEmail("new@example.com");
        user.setPassword("Password123!");
        user.setNom("New User");
        user.setRole(Role.CLIENT);

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("new@example.com", saved.getEmail());
    }
}

