package com.example.ecommerce.service;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.User;

/**
 * Interface pour la gestion du panier d'achat.
 * Définit les opérations disponibles pour la gestion du panier.
 * 
 * @author Membre 3 - Phase 4
 * @version 1.0
 */
public interface CartService {
    
    /**
     * Obtenir le panier d'un utilisateur.
     * Crée un panier si l'utilisateur n'en a pas.
     * 
     * @param user L'utilisateur
     * @return Le panier de l'utilisateur
     */
    Cart getCartByUser(User user);
    
    /**
     * Ajouter un produit au panier.
     * Vérifie le stock disponible avant l'ajout.
     * 
     * @param user L'utilisateur
     * @param productId L'ID du produit
     * @param quantity La quantité à ajouter
     * @return L'item du panier créé ou mis à jour
     */
    CartItem addProduct(User user, Long productId, int quantity);
    
    /**
     * Modifier la quantité d'un produit dans le panier.
     * Supprime le produit si la quantité est <= 0.
     * 
     * @param user L'utilisateur
     * @param productId L'ID du produit
     * @param quantity La nouvelle quantité
     * @return L'item mis à jour ou null si supprimé
     */
    CartItem updateQuantity(User user, Long productId, int quantity);
    
    /**
     * Supprimer un produit du panier.
     * 
     * @param user L'utilisateur
     * @param productId L'ID du produit à supprimer
     */
    void removeProduct(User user, Long productId);
    
    /**
     * Vider le panier.
     * 
     * @param user L'utilisateur
     */
    void clearCart(User user);
    
    /**
     * Obtenir le total du panier.
     * 
     * @param user L'utilisateur
     * @return Le total du panier
     */
    double getCartTotal(User user);
    
    /**
     * Obtenir le nombre d'articles dans le panier.
     * 
     * @param user L'utilisateur
     * @return Le nombre total d'articles
     */
    int getCartItemCount(User user);
}

