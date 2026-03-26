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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setNom("Test User");

        Category category = new Category();
        category.setId(1L);
        category.setNom("Pâtisserie");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setNom("Croissant");
        testProduct.setPrix(5.00);
        testProduct.setStock(100);
        testProduct.setDeleted(false);
        testProduct.setCategory(category);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new HashSet<>());
    }

    @Test
    @DisplayName("Devrait récupérer le panier existant")
    void testGetCartByUser_ExistingCart() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        Cart result = cartService.getCartByUser(testUser);

        assertNotNull(result);
        assertEquals(testCart, result);
        verify(cartRepository, times(1)).findByUser(testUser);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Devrait créer un nouveau panier pour un nouvel utilisateur")
    void testGetCartByUser_NewCart() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.getCartByUser(testUser);

        assertNotNull(result);
        verify(cartRepository, times(1)).findByUser(testUser);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Devrait ajouter un produit au panier")
    void testAddProduct_Success() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartAndProduct(testCart, testProduct)).thenReturn(Optional.empty());
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartItem result = cartService.addProduct(testUser, 1L, 2);

        assertNotNull(result);
        assertEquals(testProduct, result.getProduct());
        assertEquals(2, result.getQuantity());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Devrait mettre à jour la quantité")
    void testUpdateQuantity_Success() {
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(2);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartItem result = cartService.updateQuantity(testUser, 1L, 5);

        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    @Test
    @DisplayName("Devrait supprimer un produit du panier")
    void testRemoveProduct_Success() {
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(existingItem));

        cartService.removeProduct(testUser, 1L);

        verify(cartItemRepository, times(1)).delete(existingItem);
    }

    @Test
    @DisplayName("Devrait vider le panier")
    void testClearCart_Success() {
        CartItem item = new CartItem();
        item.setId(1L);
        testCart.setItems(new HashSet<>(Arrays.asList(item)));

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        cartService.clearCart(testUser);

        verify(cartItemRepository).deleteAll(anyCollection());
        assertTrue(testCart.getItems().isEmpty());
    }

    @Test
    @DisplayName("Devrait calculer le total correctement")
    void testGetCartTotal_Success() {
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

        double total = cartService.getCartTotal(testUser);

        assertEquals(20.00, total, 0.01);
    }

    @Test
    @DisplayName("Devrait retourner le nombre d'articles")
    void testGetCartItemCount_Success() {
        CartItem item1 = new CartItem();
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setQuantity(3);

        testCart.getItems().add(item1);
        testCart.getItems().add(item2);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        int count = cartService.getCartItemCount(testUser);

        assertEquals(5, count);
    }

    @Test
    @DisplayName("Devrait lever une exception pour utilisateur null")
    void testGetCartByUser_NullUser() {
        assertThrows(IllegalArgumentException.class, () -> 
            cartService.getCartByUser(null)
        );
    }
}
