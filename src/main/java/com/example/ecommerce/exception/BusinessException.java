package com.example.ecommerce.exception;

/**
 * Exception métier de base pour l'application e-commerce.
 * Remplace les RuntimeException génériques pour une meilleure gestion des erreurs.
 *
 * @author Equipe E-Commerce
 * @version 1.0
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

