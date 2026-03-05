package com.example.ecommerce.service;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;

/**
 * Service pour la gestion du panier d'achat
 * Phase 4 - Panier (Membre 3)
 */
public interface CartService {
    
    /**
     * Obtenir le panier d'un utilisateur
     */
    Cart getCartByUser(User user);
    
    /**
     * Ajouter un produit au panier
     */
    CartItem addProduct(User user, Product product, int quantity);
    
    /**
     * Modifier la quantité d'un produit dans le panier
     */
    CartItem updateQuantity(User user, Long productId, int quantity);
    
    /**
     * Supprimer un produit du panier
     */
    void removeProduct(User user, Long productId);
    
    /**
     * Vider le panier
     */
    void clearCart(User user);
    
    /**
     * Obtenir le total du panier
     */
    double getCartTotal(User user);
}

