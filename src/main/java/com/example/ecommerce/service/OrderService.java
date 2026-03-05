package com.example.ecommerce.service;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.entity.User;

import java.util.List;

/**
 * Service pour la gestion des commandes
 * Phase 5 - Commande (Membre 3)
 */
public interface OrderService {
    
    /**
     * Créer une commande à partir du panier
     */
    Order createOrder(User user);
    
    /**
     * Obtenir toutes les commandes d'un utilisateur
     */
    List<Order> getOrdersByUser(User user);
    
    /**
     * Obtenir une commande par ID
     */
    Order getOrderById(Long orderId);
    
    /**
     * Obtenir le statut d'une commande
     */
    OrderStatus getOrderStatus(Long orderId);
    
    /**
     * Mettre à jour le statut d'une commande
     */
    Order updateOrderStatus(Long orderId, OrderStatus status);
    
    /**
     * Annuler une commande (si pas encore traitée)
     */
    Order cancelOrder(Long orderId);
}

