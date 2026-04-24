package com.example.ecommerce.exception;

/**
 * Exception levée lorsqu'une commande n'est pas trouvée.
 */
public class OrderNotFoundException extends BusinessException {

    public OrderNotFoundException(Long orderId) {
        super("Commande introuvable : " + orderId);
    }
}

