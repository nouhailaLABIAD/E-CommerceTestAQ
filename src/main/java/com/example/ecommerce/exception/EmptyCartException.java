package com.example.ecommerce.exception;

/**
 * Exception levée lorsque le panier est vide lors d'une commande.
 */
public class EmptyCartException extends BusinessException {

    public EmptyCartException() {
        super("Le panier est vide");
    }
}

