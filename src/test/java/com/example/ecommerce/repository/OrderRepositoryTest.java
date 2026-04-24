package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUser_returnsOrders() {
        User user = new User();
        user.setEmail("order@example.com");
        user.setPassword("pass");
        user.setNom("Order User");
        user.setRole(Role.CLIENT);
        User savedUser = userRepository.save(user);

        Order order = new Order();
        order.setUser(savedUser);
        order.setStatus(OrderStatus.EN_COURS);
        orderRepository.save(order);

        List<Order> result = orderRepository.findByUser(savedUser);

        assertFalse(result.isEmpty());
        assertEquals(savedUser.getId(), result.get(0).getUser().getId());
    }

    @Test
    void findById_existing_returnsOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.EN_COURS);
        Order saved = orderRepository.save(order);

        Optional<Order> found = orderRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(OrderStatus.EN_COURS, found.get().getStatus());
    }

    @Test
    void findByUserEmailContainingIgnoreCase_returnsMatching() {
        User user = new User();
        user.setEmail("search@example.com");
        user.setPassword("pass");
        user.setNom("Search User");
        user.setRole(Role.CLIENT);
        User savedUser = userRepository.save(user);

        Order order = new Order();
        order.setUser(savedUser);
        order.setStatus(OrderStatus.EN_COURS);
        orderRepository.save(order);

        List<Order> result = orderRepository.findByUserEmailContainingIgnoreCase("search");

        assertFalse(result.isEmpty());
    }

    @Test
    void save_createsOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.EN_COURS);

        Order saved = orderRepository.save(order);

        assertNotNull(saved.getId());
        assertEquals(OrderStatus.EN_COURS, saved.getStatus());
    }
}

