package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour CartServiceImpl.
 * 
 * @author Membre 3
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service panier")
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;

    /**
     * Initialisation des données de test.
     */
    @BeforeEach
    void setUp() {
        // Créer un utilisateur de test
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setNom("Test User");

        // Créer une catégorie de test
        Category category = new Category();
        category.setId(1L);
        category.setNom("Pâtisserie");

        // Créer un produit de test
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setNom("Croissant");
        testProduct.setPrix(5.00);
        testProduct.setStock(100);
        testProduct.setDeleted(false);
        testProduct.setCategory(category);

        // Créer un panier de test
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new HashSet<>());
    }

    /**
     * Test : Récupérer le panier d'un utilisateur existant.
     */
    @Test
    @DisplayName("Devrait récupérer le panier existant")
    void testGetCartByUser_ExistingCart() {
        // Arrange
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        // Act
        Cart result = cartService.getCartByUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testCart, result);
        verify(cartRepository, times(1)).findByUser(testUser);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    /**
     * Test : Créer un panier pour un nouvel utilisateur.
     */
    @Test
    @DisplayName("Devrait créer un nouveau panier pour un nouvel utilisateur")
    void testGetCartByUser_NewCart() {
        // Arrange
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        Cart result = cartService.getCartByUser(testUser);

        // Assert
        assertNotNull(result);
        verify(cartRepository, times(1)).findByUser(testUser);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    /**
     * Test : Ajouter un produit au panier.
     */
    @Test
    @DisplayName("Devrait ajouter un produit au panier")
    void testAddProduct_Success() {
        // Arrange
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartAndProduct(testCart, testProduct)).thenReturn(Optional.empty());
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartItem result = cartService.addProduct(testUser, 1L, 2);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct, result.getProduct());
        assertEquals(2, result.getQuantity());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    /**
     * Test : Ajouter un produit avec stock insuffisant.
     */
    @Test
    @DisplayName("Devrait lever une exception pour stock insuffisant")
    void testAddProduct_InsufficientStock() {
        // Arrange
        testProduct.setStock(1);
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            cartService.addProduct(testUser, 1L, 5)
        );
    }

    /**
     * Test : Mettre à jour la quantité d'un produit.
     */
    @Test
    @DisplayName("Devrait mettre à jour la quantité")
    void testUpdateQuantity_Success() {
        // Arrange
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(2);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartItem result = cartService.updateQuantity(testUser, 1L, 5);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    /**
     * Test : Supprimer un produit du panier.
     */
    @Test
    @DisplayName("Devrait supprimer un produit du panier")
    void testRemoveProduct_Success() {
        // Arrange
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(existingItem));

        // Act
        cartService.removeProduct(testUser, 1L);

        // Assert
        verify(cartItemRepository, times(1)).delete(existingItem);
    }

    /**
     * Test : Vider le panier.
     */
    @Test
    @DisplayName("Devrait vider le panier")
    void testClearCart_Success() {
        // Arrange
        CartItem item = new CartItem();
        item.setId(1L);
        testCart.getItems().add(item);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        // Act
        cartService.clearCart(testUser);

        // Assert
        verify(cartItemRepository, times(1)).deleteAll(testCart.getItems());
        assertTrue(testCart.getItems().isEmpty());
    }

    /**
     * Test : Calculer le total du panier.
     */
    @Test
    @DisplayName("Devrait calculer le total correctement")
    void testGetCartTotal_Success() {
        // Arrange
        CartItem item1 = new CartItem();
        item1.setProduct(testProduct);
        item1.setQuantity(2);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setPrix(10.00);
        product2.setStock(50);
        product2.setDeleted(false);

        CartItem item2 = new CartItem();
        item2.setProduct(product2);
        item2.setQuantity(1);

        testCart.getItems().add(item1);
        testCart.getItems().add(item2);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        // Act
        double total = cartService.getCartTotal(testUser);

        // Assert
        assertEquals(20.00, total, 0.01);
    }

    /**
     * Test : Récupérer le nombre d'articles.
     */
    @Test
    @DisplayName("Devrait retourner le nombre d'articles")
    void testGetCartItemCount_Success() {
        // Arrange
        CartItem item1 = new CartItem();
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setQuantity(3);

        testCart.getItems().add(item1);
        testCart.getItems().add(item2);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        // Act
        int count = cartService.getCartItemCount(testUser);

        // Assert
        assertEquals(5, count);
    }

    /**
     * Test : Vérifier la gestion d'utilisateur null.
     */
    @Test
    @DisplayName("Devrait lever une exception pour utilisateur null")
    void testGetCartByUser_NullUser() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            cartService.getCartByUser(null)
        );
    }
}

