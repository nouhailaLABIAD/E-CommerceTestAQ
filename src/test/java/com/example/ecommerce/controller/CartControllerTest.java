package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setupUser() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void showCart_returnsCartView() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        Cart cart = new Cart();
        when(cartService.getCartByUser(testUser)).thenReturn(cart);
        when(cartService.getCartTotal(testUser)).thenReturn(25.0);
        when(cartService.getCartItemCount(testUser)).thenReturn(2);

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("cart", cart))
                .andExpect(model().attribute("total", 25.0))
                .andExpect(model().attribute("itemCount", 2));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void addToCart_success_redirectsCart() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        CartItem cartItem = new CartItem();
        when(cartService.addProduct(any(User.class), eq(1L), eq(1))).thenReturn(cartItem);

        mockMvc.perform(post("/cart/add/1")
                        .param("quantity", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void addToCart_exception_addsErrorFlash() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(cartService.addProduct(any(User.class), eq(1L), eq(1))).thenThrow(new RuntimeException("Stock insuffisant"));

        mockMvc.perform(post("/cart/add/1")
                        .param("quantity", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"))
                .andExpect(flash().attribute("error", "Stock insuffisant"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void updateQuantity_positive_success() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        CartItem cartItem = new CartItem();
        when(cartService.updateQuantity(any(User.class), eq(1L), eq(3))).thenReturn(cartItem);

        mockMvc.perform(post("/cart/update/1")
                        .param("quantity", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"))
                .andExpect(flash().attribute("success", "Quantité mise à jour"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void updateQuantity_toZero_removes_redirects() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(cartService).removeProduct(any(User.class), eq(1L));

        mockMvc.perform(post("/cart/update/1")
                        .param("quantity", "0")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"))
                .andExpect(flash().attribute("success", "Produit supprimé du panier"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void updateQuantity_exception_addsErrorFlash() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(cartService.updateQuantity(any(User.class), eq(1L), eq(3))).thenThrow(new RuntimeException("Erreur"));

        mockMvc.perform(post("/cart/update/1")
                        .param("quantity", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"))
                .andExpect(flash().attribute("error", "Erreur"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void removeFromCart_success_redirects() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(cartService).removeProduct(any(User.class), eq(1L));

        mockMvc.perform(post("/cart/remove/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void removeFromCart_exception_addsErrorFlash() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("Erreur suppression")).when(cartService).removeProduct(any(User.class), eq(1L));

        mockMvc.perform(post("/cart/remove/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"))
                .andExpect(flash().attribute("error", "Erreur suppression"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void clearCart_success_redirects() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(cartService).clearCart(any(User.class));

        mockMvc.perform(post("/cart/clear")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void clearCart_exception_addsErrorFlash() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("Erreur vidage")).when(cartService).clearCart(any(User.class));

        mockMvc.perform(post("/cart/clear")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"))
                .andExpect(flash().attribute("error", "Erreur vidage"));
    }

    @Test
    void showCart_notLoggedIn_redirectsLogin() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());
    }
}

