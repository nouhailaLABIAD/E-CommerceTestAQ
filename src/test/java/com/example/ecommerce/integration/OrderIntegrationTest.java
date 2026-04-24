package com.example.ecommerce.integration;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(
    scripts = {
        "classpath:test-data/categories.sql",
        "classpath:test-data/users.sql",
        "classpath:test-data/products.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @WithMockUser(username = "client@test.com")
    void fullOrderFlow_integration() throws Exception {

        // ✅ 1. Verify user exists
        User user = userRepository.findByEmail("client@test.com")
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ 2. Verify product exists
        Product product = productRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Act: Access order history
        mockMvc.perform(get("/orders/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-history"));

        // ✅ 3. Verify DB state
        List<Order> orders = orderRepository.findByUser(user);

        assertNotNull(orders);
        // ممكن حتى:
        // assertTrue(orders.size() >= 0);
    }
}