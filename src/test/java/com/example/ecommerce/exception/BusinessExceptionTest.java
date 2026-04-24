package com.example.ecommerce.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void constructor_withMessage_setsMessage() {
        BusinessException ex = new BusinessException("Erreur test");
        assertEquals("Erreur test", ex.getMessage());
    }

    @Test
    void constructor_withMessageAndCause_setsBoth() {
        Throwable cause = new IllegalStateException("Cause");
        BusinessException ex = new BusinessException("Erreur test", cause);
        assertEquals("Erreur test", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}

