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
import static org.mockito.Mockito.*;
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
                .andExpect(redirectedUrl("/cart"))
                .andExpect(flash().attribute("error", "Stock insuffisant"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void orderHistory_returnsHistoryView() throws Exception {
        setupUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(testUser));

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
        order.setItems(Set.of());
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1/confirmation"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-confirmation"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void orderConfirmation_differentUser_redirectsCart() throws Exception {
        setupUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(testUser));

        User otherUser = new User();
        otherUser.setId(2L);

        Order order = new Order();
        order.setId(1L);
        order.setUser(otherUser);
        order.setItems(Set.of());
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1/confirmation"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void orderConfirmation_exception_redirectsCart() throws Exception {
        setupUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(testUser));
        when(orderService.getOrderById(1L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/orders/1/confirmation"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
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
                .andExpect(redirectedUrl("/orders/history"))
                .andExpect(flash().attribute("success", "Commande annulée avec succès"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void cancelOrder_differentUser_redirectsHistory() throws Exception {
        setupUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(testUser));

        User otherUser = new User();
        otherUser.setId(2L);

        Order order = new Order();
        order.setId(1L);
        order.setUser(otherUser);
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(post("/orders/1/cancel").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/history"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void cancelOrder_exception_addsErrorFlash() throws Exception {
        setupUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(testUser));

        Order order = new Order();
        order.setId(1L);
        order.setUser(testUser);
        when(orderService.getOrderById(1L)).thenReturn(order);
        doThrow(new RuntimeException("Impossible d'annuler")).when(orderService).cancelOrder(1L);

        mockMvc.perform(post("/orders/1/cancel").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/history"))
                .andExpect(flash().attribute("error", "Impossible d'annuler"));
    }

    private Order createOrderWithItems(OrderStatus status) {
        Order order = new Order();
        order.setStatus(status);
        order.setItems(Set.of());
        return order;
    }
}

