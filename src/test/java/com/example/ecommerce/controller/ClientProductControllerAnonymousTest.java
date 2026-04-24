package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClientProductControllerAnonymousTest {

    private MockMvc mockMvc;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private CartService cartService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        productRepository = org.mockito.Mockito.mock(ProductRepository.class);
        categoryRepository = org.mockito.Mockito.mock(CategoryRepository.class);
        cartService = org.mockito.Mockito.mock(CartService.class);
        userRepository = org.mockito.Mockito.mock(UserRepository.class);

        org.thymeleaf.spring6.SpringTemplateEngine templateEngine = new org.thymeleaf.spring6.SpringTemplateEngine();
        org.thymeleaf.spring6.view.ThymeleafViewResolver viewResolver = new org.thymeleaf.spring6.view.ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new ClientProductController(productRepository, categoryRepository, cartService, userRepository))
                .setViewResolvers(viewResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void homePatisserie_anonymous_returnsViewWithCartItemCountZero() throws Exception {
        when(categoryRepository.findAll()).thenReturn(List.of(new Category()));
        when(productRepository.findByDeletedFalse()).thenReturn(List.of(new Product()));

        mockMvc.perform(get("/client/homePatisserie"))
                .andExpect(status().isOk())
                .andExpect(view().name("homePatisserie"))
                .andExpect(model().attribute("cartItemCount", 0));
    }

    @Test
    void products_anonymous_returnsViewWithCartItemCountZero() throws Exception {
        when(categoryRepository.findAll()).thenReturn(List.of(new Category()));
        when(productRepository.findByDeletedFalse()).thenReturn(List.of(new Product()));

        mockMvc.perform(get("/client/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("client-products"))
                .andExpect(model().attribute("cartItemCount", 0));
    }

    @Test
    void searchProducts_anonymous_returnsViewWithCartItemCountZero() throws Exception {
        when(categoryRepository.findAll()).thenReturn(List.of(new Category()));
        when(productRepository.findByNomContainingIgnoreCaseAndDeletedFalse("cake")).thenReturn(List.of(new Product()));

        mockMvc.perform(get("/client/products/search").param("keyword", "cake"))
                .andExpect(status().isOk())
                .andExpect(view().name("client-products"))
                .andExpect(model().attribute("cartItemCount", 0));
    }

    @Test
    void productDetail_anonymous_returnsViewWithCartItemCountZero() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setDeleted(false);
        Category category = new Category();
        category.setId(1L);
        product.setCategory(category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findByDeletedFalse()).thenReturn(List.of());

        mockMvc.perform(get("/client/products/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-detail"))
                .andExpect(model().attribute("cartItemCount", 0));
    }
}

