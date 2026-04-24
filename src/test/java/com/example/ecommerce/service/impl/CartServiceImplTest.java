package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.exception.ProductUnavailableException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

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

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setNom("Croissant");
        testProduct.setPrix(5.0);
        testProduct.setStock(10);
        testProduct.setDeleted(false);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new HashSet<>());
    }

    @Test
    void addProduct_existingItem_updatesQuantity() {
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(2);
        testCart.getItems().add(existingItem);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartAndProduct(testCart, testProduct))
                .thenReturn(Optional.of(existingItem));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CartItem result = cartService.addProduct(testUser, 1L, 3);

        assertEquals(5, result.getQuantity());
    }

    @Test
    void addProduct_existingItem_insufficientStock_throws() {
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(8);
        testCart.getItems().add(existingItem);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartAndProduct(testCart, testProduct))
                .thenReturn(Optional.of(existingItem));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(InsufficientStockException.class,
                () -> cartService.addProduct(testUser, 1L, 5));
    }

    @Test
    void addProduct_productNotFound_throws() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> cartService.addProduct(testUser, 1L, 1));
    }

    @Test
    void addProduct_productDeleted_throws() {
        testProduct.setDeleted(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(ProductUnavailableException.class,
                () -> cartService.addProduct(testUser, 1L, 1));
    }

    @Test
    void addProduct_insufficientStock_throws() {
        testProduct.setStock(1);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(InsufficientStockException.class,
                () -> cartService.addProduct(testUser, 1L, 5));
    }

    @Test
    void updateQuantity_insufficientStock_throws() {
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(1);
        testCart.getItems().add(existingItem);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(existingItem));

        assertThrows(InsufficientStockException.class,
                () -> cartService.updateQuantity(testUser, 1L, 20));
    }

    @Test
    void getCartByUser_nullUser_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.getCartByUser(null));
    }

    @Test
    void getCartTotal_nullUser_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.getCartTotal(null));
    }

    @Test
    void getCartItemCount_nullUser_returnsZero() {
        assertEquals(0, cartService.getCartItemCount(null));
    }

    @Test
    void clearCart_nullUser_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.clearCart(null));
    }

    @Test
    void addProduct_nullUser_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addProduct(null, 1L, 1));
    }

    @Test
    void addProduct_nullProductId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addProduct(testUser, null, 1));
    }

    @Test
    void addProduct_invalidQuantity_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addProduct(testUser, 1L, 0));
    }
}

