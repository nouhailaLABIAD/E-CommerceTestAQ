package com.example.ecommerce.controller;

import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartControllerAnonymousTest {

    private MockMvc mockMvc;
    private CartService cartService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        cartService = org.mockito.Mockito.mock(CartService.class);
        userRepository = org.mockito.Mockito.mock(UserRepository.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CartController(cartService, userRepository))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void showCart_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirect=/cart"));
    }

    @Test
    void addToCart_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/cart/add/1")
                        .param("quantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirect=/products"));
    }

    @Test
    void updateQuantity_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/cart/update/1")
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirect=/cart"));
    }

    @Test
    void removeFromCart_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/cart/remove/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirect=/cart"));
    }

    @Test
    void clearCart_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/cart/clear"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirect=/cart"));
    }
}

