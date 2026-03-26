package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import org.junit.jupiter.api.BeforeEach;
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
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Cart testCart;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testCart = new Cart();
        testCart.setUser(testUser);
        testCart.setItems(new HashSet<>());

        testProduct1 = new Product();
        testProduct1.setId(1L);
        testProduct1.setNom("Product 1");
        testProduct1.setPrix(10.0);
        testProduct1.setStock(20);

        testProduct2 = new Product();
        testProduct2.setId(2L);
        testProduct2.setNom("Product 2");
        testProduct2.setPrix(15.0);
        testProduct2.setStock(10);

        CartItem cartItem1 = new CartItem();
        cartItem1.setProduct(testProduct1);
        cartItem1.setQuantity(2);
        testCart.getItems().add(cartItem1);

        CartItem cartItem2 = new CartItem();
        cartItem2.setProduct(testProduct2);
        cartItem2.setQuantity(1);
        testCart.getItems().add(cartItem2);
    }

    @Test
    void createOrder_success() {
        // Arrange
        when(cartService.getCartByUser(testUser)).thenReturn(testCart);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            order.setId(1L);
            return order;
        });
        when(orderItemRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // Act
        Order result = orderService.createOrder(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.EN_COURS, result.getStatus());
        assertEquals(testUser, result.getUser());
        verify(productRepository, times(2)).save(any(Product.class)); // stock updated
        verify(cartService).clearCart(testUser);
    }

    @Test
    void createOrder_emptyCart_throwsException() {
        // Arrange
        testCart.setItems(new HashSet<>());
        when(cartService.getCartByUser(testUser)).thenReturn(testCart);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.createOrder(testUser));
    }

    @Test
    void createOrder_lowStock_throwsException() {
        // Arrange
        testProduct1.setStock(1); // low stock
        when(cartService.getCartByUser(testUser)).thenReturn(testCart);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.createOrder(testUser));
    }

    @Test
    void getOrdersByUser() {
        // Arrange
        List<Order> expectedOrders = List.of(new Order(), new Order());
        when(orderRepository.findByUser(testUser)).thenReturn(expectedOrders);

        // Act
        List<Order> result = orderService.getOrdersByUser(testUser);

        // Assert
        assertEquals(expectedOrders, result);
    }

    @Test
    void getOrderById_notFound_throwsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void updateOrderStatus() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order updatedOrder = orderService.updateOrderStatus(1L, OrderStatus.VALIDEE);

        // Assert
        assertEquals(OrderStatus.VALIDEE, updatedOrder.getStatus());
    }

    @Test
    void cancelOrder_success_restoresStock() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.EN_COURS);
        OrderItem item = new OrderItem();
        item.setQuantity(3);
        item.setProduct(testProduct1);
        order.setItems(Set.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        orderService.cancelOrder(1L);

        // Assert
        assertEquals(OrderStatus.ANNULEE, order.getStatus());
        verify(productRepository).save(testProduct1); // stock restored
    }

    @Test
    void cancelOrder_notEnCours_throwsException() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.VALIDEE);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(1L));
    }
}
