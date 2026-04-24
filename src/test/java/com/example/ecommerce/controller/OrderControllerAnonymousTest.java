package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerAnonymousTest {

    private MockMvc mockMvc;
    private OrderService orderService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        orderService = org.mockito.Mockito.mock(OrderService.class);
        userRepository = org.mockito.Mockito.mock(UserRepository.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService, userRepository))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createOrder_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/orders/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirect=/cart"));
    }

    @Test
    void orderConfirmation_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/orders/1/confirmation"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void orderHistory_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/orders/history"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void cancelOrder_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/orders/1/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}

