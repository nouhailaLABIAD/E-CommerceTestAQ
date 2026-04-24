package com.example.ecommerce.controller;

import com.example.ecommerce.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Gère les exceptions métier (logique applicativa).
     * Retourne une erreur 400 Bad Request avec le message métier.
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusinessException(BusinessException ex, Model model) {
        logger.warn("Erreur métier : {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    /**
     * Gère les exceptions techniques non anticipées.
     * Log l'erreur complète pour investigation.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        logger.error("Erreur technique inattendue : {}", ex.getMessage(), ex);
        model.addAttribute("error", "Une erreur interne est survenue. Veuillez réessayer plus tard.");
        return "error";
    }
}

