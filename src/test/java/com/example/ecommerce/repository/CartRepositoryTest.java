package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUser_existing_returnsCart() {
        User user = new User();
        user.setEmail("cart@example.com");
        user.setPassword("Password123!");
        user.setNom("Cart User");
        user.setRole(Role.CLIENT);
        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        Optional<Cart> found = cartRepository.findByUser(savedUser);

        assertTrue(found.isPresent());
        assertEquals(savedUser.getId(), found.get().getUser().getId());
    }

    @Test
    void findByUser_nonExisting_returnsEmpty() {
        User user = new User();
        user.setId(999L);

        Optional<Cart> found = cartRepository.findByUser(user);

        assertFalse(found.isPresent());
    }

    @Test
    void save_createsCart() {
        User user = new User();
        user.setEmail("newcart@example.com");
        user.setPassword("Password123!");
        user.setNom("New Cart User");
        user.setRole(Role.CLIENT);
        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);

        Cart saved = cartRepository.save(cart);

        assertNotNull(saved.getId());
        assertEquals(savedUser.getId(), saved.getUser().getId());
    }
}

