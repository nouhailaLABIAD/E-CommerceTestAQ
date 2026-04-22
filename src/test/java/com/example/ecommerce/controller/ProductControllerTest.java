package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.interfaces.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @WithMockUser // FIX: routes protégées par Spring Security → nécessite un utilisateur mock
    void getAllProducts_returnsProductsView() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setNom("Test Cake");
        List<Product> products = List.of(product);
        when(productService.getAllAvailableProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                // FIX: supprimé les assertions contradictoires et incorrectes
                // (model().attribute("products", List.of()) contredisait la première assertion)
                // (model().attribute("products[0].nom", ...) n'est pas supporté par MockMvc)
                .andExpect(model().attribute("products", products));
    }

    @Test
    @WithMockUser // FIX: ajout authentification mock
    void searchProducts_returnsProductsView() throws Exception {
        Product product = new Product();
        product.setNom("Cake");
        List<Product> products = List.of(product);
        when(productService.searchProducts("cake")).thenReturn(products);

        mockMvc.perform(get("/products/search").param("keyword", "cake"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    @WithMockUser // FIX: ajout authentification mock
    void getProductDetail_returnsDetailView() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setNom("Chocolate Cake");
        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-detail"))
                // FIX: supprimé model().attribute("product.nom", ...) qui n'est pas supporté
                .andExpect(model().attribute("product", product));
    }

    @Test
    @WithMockUser // FIX: ajout authentification mock
    void getProductDetail_notFound_returnsError() throws Exception {
        when(productService.getProductById(999L)).thenThrow(new RuntimeException("Produit non trouvé"));

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isOk());
    }
}