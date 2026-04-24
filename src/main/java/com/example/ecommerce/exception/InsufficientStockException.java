package com.example.ecommerce.exception;

/**
 * Exception levée lorsque le stock est insuffisant pour un produit.
 */
public class InsufficientStockException extends BusinessException {

    public InsufficientStockException(String productName, int availableStock) {
        super("Stock insuffisant pour : " + productName + " (disponible : " + availableStock + ")");
    }

    public InsufficientStockException(int availableStock) {
        super("Stock insuffisant. Disponible: " + availableStock);
    }
}

