package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

/**
 * Implémentation du service de gestion du panier d'achat.
 * Gère l'ajout, la modification et la suppression de produits dans le panier.
 * 
 * @author Membre 3 - Phase 4
 * @version 1.0
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * Constructeur avec injection des dépendances.
     * 
     * @param cartRepository Repository pour les paniers
     * @param cartItemRepository Repository pour les items du panier
     * @param productRepository Repository pour les produits
     */
    public CartServiceImpl(CartRepository cartRepository, 
                          CartItemRepository cartItemRepository,
                          ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cart getCartByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }
        
        return cartRepository.findByUser(user)
                .orElseGet(() -> createCartForUser(user));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CartItem addProduct(User user, Long productId, int quantity) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }
        
        if (productId == null) {
            throw new IllegalArgumentException("Le produit ne peut pas être null");
        }
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0");
        }

        // Récupérer le produit depuis la base de données
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + productId));
        
        // Vérifier si le produit n'est pas supprimé
        if (product.isDeleted()) {
            throw new RuntimeException("Ce produit n'est plus disponible");
        }
        
        // Vérifier le stock
        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuffisant. Disponible: " + product.getStock());
        }

        Cart cart = getCartByUser(user);
        
        // Vérifier si le produit existe déjà dans le panier
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            
            // Vérifier le stock pour la nouvelle quantité
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Stock insuffisant pour cette quantité");
            }
            
            item.setQuantity(newQuantity);
            return cartItemRepository.save(item);
        } else {
            // Créer un nouvel item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            return cartItemRepository.save(newItem);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CartItem updateQuantity(User user, Long productId, int quantity) {
        if (user == null || productId == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        Cart cart = getCartByUser(user);
        
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé dans le panier"));
        
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            cart.getItems().remove(item);
            return null;
        }
        
        // Vérifier le stock
        Product product = item.getProduct();
        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuffisant. Disponible: " + product.getStock());
        }
        
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProduct(User user, Long productId) {
        if (user == null || productId == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        Cart cart = getCartByUser(user);
        
        cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .ifPresent(item -> {
                    cart.getItems().remove(item);
                    cartItemRepository.delete(item);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearCart(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }

        Cart cart = getCartByUser(user);
        
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getItems());
            cart.setItems(new HashSet<>());
            cartRepository.save(cart);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getCartTotal(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }

        Cart cart = getCartByUser(user);
        
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return 0.0;
        }
        
        return cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrix() * item.getQuantity())
                .sum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCartItemCount(User user) {
        if (user == null) {
            return 0;
        }

        Cart cart = getCartByUser(user);
        
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return 0;
        }
        
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Crée un nouveau panier pour un utilisateur.
     * 
     * @param user L'utilisateur
     * @return Le panier créé
     */
    private Cart createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new HashSet<>());
        return cartRepository.save(cart);
    }
}

