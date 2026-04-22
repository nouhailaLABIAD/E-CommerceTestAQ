package com.example.ecommerce.controller;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserRepository userRepository;

    private User testUser;

    void setupUser() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createOrder_success_redirectsToConfirmation() throws Exception {
        setupUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(testUser));

        Order order = new Order();
        order.setId(1L);
        order.setUser(testUser);
        when(orderService.createOrder(any(User.class))).thenReturn(order);

        mockMvc.perform(post("/orders/create").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/1/confirmation"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createOrder_error_redirectsToCart() throws Exception {
        setupUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(testUser));
        when(orderService.createOrder(any(User.class))).thenThrow(new RuntimeException("Stock insuffisant"));

        mockMvc.perform(post("/orders/create").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void orderHistory_returnsHistoryView() throws Exception {
        setupUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(testUser));

        // FIX: l'ordre doit avoir des items non-null pour éviter l'erreur
        // #aggregates.sum(order.items.![product.prix * quantity]) dans order-history.html
        List<Order> orders = List.of(createOrderWithItems(OrderStatus.EN_COURS));
        when(orderService.getOrdersByUser(testUser)).thenReturn(orders);

        mockMvc.perform(get("/orders/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-history"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void orderConfirmation_success() throws Exception {
        setupUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(testUser));

        Order order = new Order();
        order.setId(1L);
        order.setUser(testUser);
        order.setStatus(OrderStatus.EN_COURS);
        // FIX: items non-null pour éviter NullPointerException dans le template
        order.setItems(Set.of());
        when(orderService.getOrderById(1L)).thenReturn(order);

        // FIX: le controller vérifie que la commande appartient à l'utilisateur connecté.
        // Avec @WithMockUser, le principal.name = "test@example.com" mais userRepository
        // doit retourner l'utilisateur correspondant.
        // Si le controller redirige (302) c'est que l'utilisateur ne correspond pas → on aligne les IDs.
        mockMvc.perform(get("/orders/1/confirmation"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-confirmation"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void cancelOrder_success() throws Exception {
        setupUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(testUser));

        Order order = new Order();
        order.setId(1L);
        order.setUser(testUser);
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(orderService.cancelOrder(1L)).thenReturn(order);

        mockMvc.perform(post("/orders/1/cancel").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/history"));
    }

    // FIX: helper avec items initialisés (liste vide) pour éviter NullPointerException
    // dans le template Thymeleaf sur #aggregates.sum(order.items.![...])
    private Order createOrderWithItems(OrderStatus status) {
        Order order = new Order();
        order.setStatus(status);
        order.setItems(Set.of()); // liste vide mais non-null
        return order;
    }
}