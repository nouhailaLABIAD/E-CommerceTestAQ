package com.example.ecommerce.exception;

/**
 * Exception levée lorsqu'un produit n'est pas trouvé.
 */
public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException(Long productId) {
        super("Produit non trouvé avec l'ID: " + productId);
    }
}

