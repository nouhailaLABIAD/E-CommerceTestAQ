package com.example.ecommerce.exception;

/**
 * Exception levée lorsqu'une catégorie n'est pas trouvée.
 */
public class CategoryNotFoundException extends BusinessException {

    public CategoryNotFoundException(Long categoryId) {
        super("Catégorie introuvable : " + categoryId);
    }
}

