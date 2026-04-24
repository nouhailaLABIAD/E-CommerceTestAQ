package com.example.ecommerce.controller;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatisserieController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatisserieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    void homePatisserie_authenticated_returnsViewWithUser() throws Exception {
        User user = new User();
        user.setEmail("client@test.com");
        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/patisserie"))
                .andExpect(status().isOk())
                .andExpect(view().name("homePatisserie"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    @WithMockUser(username = "unknown@test.com", roles = "CLIENT")
    void homePatisserie_authenticatedUserNotFound_returnsView() throws Exception {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/patisserie"))
                .andExpect(status().isOk())
                .andExpect(view().name("homePatisserie"));
    }

    @Test
    @WithAnonymousUser
    void homePatisserie_anonymous_returnsView() throws Exception {
        mockMvc.perform(get("/patisserie"))
                .andExpect(status().isOk())
                .andExpect(view().name("homePatisserie"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void addToCart_redirects() throws Exception {
        mockMvc.perform(get("/patisserie/add")
                        .param("productId", "1")
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patisserie?added=1"));
    }
}

