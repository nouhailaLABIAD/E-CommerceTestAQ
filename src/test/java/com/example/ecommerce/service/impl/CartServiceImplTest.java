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
    void getCartByUser_existingCart_returnsCart() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        Cart result = cartService.getCartByUser(testUser);
        assertEquals(testCart, result);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCartByUser_noCart_createsNewCart() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> {
            Cart c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });

        Cart result = cartService.getCartByUser(testUser);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertNotNull(result.getItems());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addProduct_newItem_createsItem() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartAndProduct(testCart, testProduct)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        CartItem result = cartService.addProduct(testUser, 1L, 2);

        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertEquals(testProduct, result.getProduct());
        assertEquals(testCart, result.getCart());
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
    void updateQuantity_success() {
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(1);
        testCart.getItems().add(existingItem);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        CartItem result = cartService.updateQuantity(testUser, 1L, 3);

        assertNotNull(result);
        assertEquals(3, result.getQuantity());
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
    void updateQuantity_toZero_deletesItem() {
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(1);
        testCart.getItems().add(existingItem);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(existingItem));
        doNothing().when(cartItemRepository).delete(existingItem);

        CartItem result = cartService.updateQuantity(testUser, 1L, 0);

        assertNull(result);
        verify(cartItemRepository).delete(existingItem);
    }

    @Test
    void removeProduct_success() {
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);
        testCart.getItems().add(existingItem);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(existingItem));
        doNothing().when(cartItemRepository).delete(existingItem);

        cartService.removeProduct(testUser, 1L);

        verify(cartItemRepository).delete(existingItem);
        assertFalse(testCart.getItems().contains(existingItem));
    }

    @Test
    void removeProduct_itemNotFound_doesNothing() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L))
                .thenReturn(Optional.empty());

        cartService.removeProduct(testUser, 1L);

        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    void clearCart_withItems_clearsAll() {
        CartItem item1 = new CartItem();
        CartItem item2 = new CartItem();
        testCart.getItems().add(item1);
        testCart.getItems().add(item2);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        doNothing().when(cartItemRepository).deleteAll(any());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        cartService.clearCart(testUser);

        verify(cartItemRepository).deleteAll(any());
        verify(cartRepository).save(testCart);
        assertTrue(testCart.getItems().isEmpty());
    }

    @Test
    void clearCart_emptyItems_doesNothing() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        cartService.clearCart(testUser);

        verify(cartItemRepository, never()).deleteAll(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCartTotal_withItems_returnsSum() {
        Product p1 = new Product();
        p1.setPrix(10.0);
        Product p2 = new Product();
        p2.setPrix(5.0);

        CartItem item1 = new CartItem();
        item1.setProduct(p1);
        item1.setQuantity(2);
        CartItem item2 = new CartItem();
        item2.setProduct(p2);
        item2.setQuantity(3);

        testCart.getItems().add(item1);
        testCart.getItems().add(item2);

        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        double total = cartService.getCartTotal(testUser);

        assertEquals(35.0, total, 0.01);
    }

    @Test
    void getCartTotal_emptyItems_returnsZero() {
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        double total = cartService.getCartTotal(testUser);

        assertEquals(0.0, total);
    }

    @Test
    void getCartItemCount_withItems_returnsSum() {
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
    void clearCart_nullItems_doesNothing() {
        testCart.setItems(null);
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        cartService.clearCart(testUser);

        verify(cartItemRepository, never()).deleteAll(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCartTotal_nullItems_returnsZero() {
        testCart.setItems(null);
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        double total = cartService.getCartTotal(testUser);

        assertEquals(0.0, total);
    }

    @Test
    void getCartItemCount_nullItems_returnsZero() {
        testCart.setItems(null);
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        int count = cartService.getCartItemCount(testUser);

        assertEquals(0, count);
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

