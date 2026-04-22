package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.service.FileStorageService;
import com.example.ecommerce.service.interfaces.CategoryService;
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

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void listCategories_returnsView() throws Exception {
        Category cat = new Category();
        cat.setId(1L);
        cat.setNom("Pâtisserie");
        List<Category> categories = List.of(cat);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-categories"))
                .andExpect(model().attribute("categories", categories));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showCreateForm_returnsForm() throws Exception {
        mockMvc.perform(get("/admin/categories/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("category-form"))
                .andExpect(model().attribute("category", isA(Category.class)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_withImage_redirects() throws Exception {
        MockMultipartFile image = new MockMultipartFile("imageFile", "test.jpg", "image/jpeg", "test".getBytes());
        when(fileStorageService.saveImage(any(), eq("categories"))).thenReturn("/uploads/categories/test.jpg");

        mockMvc.perform(multipart("/admin/categories")
                        .file(image)
                        .param("nom", "New Cat")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"));

        verify(categoryService).createCategory(any(Category.class));
        verify(fileStorageService).saveImage(any(), eq("categories"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showEditForm_returnsForm() throws Exception {
        Category cat = new Category();
        cat.setId(1L);
        cat.setNom("Old");
        when(categoryService.getCategoryById(1L)).thenReturn(cat);

        mockMvc.perform(get("/admin/categories/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("category-form"))
                .andExpect(model().attribute("category.nom", "Old"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory_noNewImage_keepsOld_redirects() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(new Category());

        mockMvc.perform(post("/admin/categories/edit/1")
                        .param("nom", "Updated")
                        .param("existingImageUrl", "/old.jpg")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_redirects() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(get("/admin/categories/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"));

        verify(categoryService).deleteCategory(1L);
    }
}

