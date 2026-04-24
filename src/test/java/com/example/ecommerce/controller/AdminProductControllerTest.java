package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.FileStorageService;
import com.example.ecommerce.service.interfaces.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AdminProductController.class)
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private com.example.ecommerce.repository.CategoryRepository categoryRepository;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void listProducts_returnsView() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setNom("Test Product");
        List<Product> products = List.of(product);
        when(productService.getAllAvailableProducts()).thenReturn(products);

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-products"))
                .andExpect(model().attribute("products", products));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showCreateForm_returnsForm() throws Exception {
        List<Category> categories = List.of(new Category());
        when(categoryRepository.findAll()).thenReturn(categories);

        mockMvc.perform(get("/admin/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-form"))
                .andExpect(model().attribute("product", org.hamcrest.Matchers.instanceOf(Product.class)))
                .andExpect(model().attribute("categories", categories));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_withImage_redirects() throws Exception {
        MockMultipartFile image = new MockMultipartFile("imageFile", "test.jpg", "image/jpeg", "test".getBytes());
        when(fileStorageService.saveImage(any(), eq("products"))).thenReturn("/uploads/products/test.jpg");
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(new Category()));

        mockMvc.perform(multipart("/admin/products")
                        .file(image)
                        .param("nom", "New Product")
                        .param("prix", "10.0")
                        .param("stock", "100")
                        .param("categoryId", "1")
                        .param("description", "Test desc")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_categoryNull_returnsFormWithError() throws Exception {
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        when(categoryRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(multipart("/admin/products")
                        .file(new MockMultipartFile("imageFile", "", "image/jpeg", new byte[0]))
                        .param("nom", "New Product")
                        .param("prix", "10.0")
                        .param("stock", "100")
                        .param("categoryId", "1")
                        .param("description", "Test desc")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("product-form"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showEditForm_returnsForm() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setNom("Old Product");
        when(productService.getProductById(1L)).thenReturn(product);
        when(categoryRepository.findAll()).thenReturn(List.of(new Category()));

        mockMvc.perform(get("/admin/products/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-form"))
                .andExpect(model().attribute("product", product))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_redirects() throws Exception {
        when(productService.getProductById(1L)).thenReturn(new Product());
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(new Category()));

        mockMvc.perform(post("/admin/products/edit/1")
                        .param("nom", "Updated")
                        .param("description", "Updated description")
                        .param("prix", "20.0")
                        .param("stock", "50")
                        .param("categoryId", "1")
                        .param("existingImageUrl", "/old.jpg")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_withNewImage_deletesOldAndSavesNew() throws Exception {
        when(productService.getProductById(1L)).thenReturn(new Product());
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(new Category()));
        when(fileStorageService.saveImage(any(), eq("products"))).thenReturn("/uploads/products/new.jpg");

        MockMultipartFile image = new MockMultipartFile("imageFile", "new.jpg", "image/jpeg", "test".getBytes());

        mockMvc.perform(multipart("/admin/products/edit/1")
                        .file(image)
                        .param("nom", "Updated")
                        .param("description", "Desc")
                        .param("prix", "20.0")
                        .param("stock", "50")
                        .param("categoryId", "1")
                        .param("existingImageUrl", "/old.jpg")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        verify(fileStorageService).deleteImage("/old.jpg");
        verify(fileStorageService).saveImage(any(), eq("products"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_categoryNull_returnsFormWithError() throws Exception {
        Product product = new Product();
        product.setId(1L);
        when(productService.getProductById(1L)).thenReturn(product);
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        when(categoryRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(post("/admin/products/edit/1")
                        .param("nom", "Updated")
                        .param("description", "Desc")
                        .param("prix", "20.0")
                        .param("stock", "50")
                        .param("categoryId", "1")
                        .param("existingImageUrl", "/old.jpg")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("product-form"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_redirects() throws Exception {
        doNothing().when(productService).softDeleteProduct(1L);

        mockMvc.perform(get("/admin/products/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        verify(productService).softDeleteProduct(1L);
    }
}

