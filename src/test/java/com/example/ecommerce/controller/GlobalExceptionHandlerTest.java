package com.example.ecommerce.controller;

import com.example.ecommerce.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test-business")
        public String throwBusiness() {
            throw new BusinessException("Erreur métier test");
        }

        @GetMapping("/test-runtime")
        public String throwRuntime() {
            throw new RuntimeException("Erreur technique test");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleBusinessException_returnsErrorView() throws Exception {
        mockMvc.perform(get("/test-business"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("error", "Erreur métier test"));
    }

    @Test
    void handleRuntimeException_returnsErrorView() throws Exception {
        mockMvc.perform(get("/test-runtime"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("error", "Une erreur interne est survenue. Veuillez réessayer plus tard."));
    }
}
