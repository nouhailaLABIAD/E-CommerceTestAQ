package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientProductController.class)
class ClientProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    void homePatisserie_returnsView() throws Exception {
        User user = new User();
        user.setEmail("client@test.com");
        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(user));
        when(categoryRepository.findAll()).thenReturn(List.of(new Category()));
        when(productRepository.findByDeletedFalse()).thenReturn(List.of(new Product()));
        when(cartService.getCartItemCount(user)).thenReturn(2);

        mockMvc.perform(get("/client/homePatisserie"))
                .andExpect(status().isOk())
                .andExpect(view().name("homePatisserie"))
                .andExpect(model().attributeExists("categories", "featuredProducts", "user", "cartItemCount"));
    }

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    void products_withoutCategory_returnsView() throws Exception {
        User user = new User();
        user.setEmail("client@test.com");
        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(user));
        when(categoryRepository.findAll()).thenReturn(List.of(new Category()));
        when(productRepository.findByDeletedFalse()).thenReturn(List.of(new Product()));

        mockMvc.perform(get("/client/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("client-products"))
                .andExpect(model().attributeExists("products", "categories"));
    }

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    void products_withCategory_returnsView() throws Exception {
        User user = new User();
        user.setEmail("client@test.com");
        Category category = new Category();
        category.setId(1L);
        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(user));
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(productRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/client/products").param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("client-products"))
                .andExpect(model().attributeExists("products", "categories", "selectedCategory"));
    }

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    void searchProducts_returnsView() throws Exception {
        User user = new User();
        user.setEmail("client@test.com");
        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(user));
        when(categoryRepository.findAll()).thenReturn(List.of(new Category()));
        when(productRepository.findByNomContainingIgnoreCaseAndDeletedFalse("cake")).thenReturn(List.of(new Product()));

        mockMvc.perform(get("/client/products/search").param("keyword", "cake"))
                .andExpect(status().isOk())
                .andExpect(view().name("client-products"))
                .andExpect(model().attributeExists("products", "searchKeyword"));
    }

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    void productDetail_existing_returnsView() throws Exception {
        User user = new User();
        user.setEmail("client@test.com");
        Product product = new Product();
        product.setId(1L);
        product.setDeleted(false);
        Category category = new Category();
        category.setId(1L);
        product.setCategory(category);
        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findByDeletedFalse()).thenReturn(List.of());

        mockMvc.perform(get("/client/products/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-detail"))
                .andExpect(model().attribute("product", product));
    }

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    void productDetail_deleted_redirects() throws Exception {
        User user = new User();
        user.setEmail("client@test.com");
        Product product = new Product();
        product.setId(1L);
        product.setDeleted(true);
        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/client/products/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/client/products"));
    }
}

