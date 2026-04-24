package com.example.ecommerce.exception;

/**
 * Exception levée lorsqu'un produit n'est plus disponible (soft-deleted).
 */
public class ProductUnavailableException extends BusinessException {

    public ProductUnavailableException() {
        super("Ce produit n'est plus disponible");
    }
}

