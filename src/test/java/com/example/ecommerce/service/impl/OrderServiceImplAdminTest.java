package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.OrderNotFoundException;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplAdminTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("client@test.com");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setStatus(OrderStatus.EN_COURS);
        testOrder.setUser(testUser);
    }

    @Test
    void getAllOrders_returnsAll() {
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void getAdminOrderById_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Order result = orderService.getAdminOrderById(1L);

        assertEquals(testOrder, result);
    }

    @Test
    void getAdminOrderById_notFound_throwsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getAdminOrderById(999L));
    }

    @Test
    void searchOrdersByUserEmail_nullEmail_returnsAll() {
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));

        List<Order> result = orderService.searchOrdersByUserEmail(null);

        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void searchOrdersByUserEmail_emptyEmail_returnsAll() {
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));

        List<Order> result = orderService.searchOrdersByUserEmail("   ");

        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void searchOrdersByUserEmail_withEmail_returnsFiltered() {
        when(orderRepository.findByUserEmailContainingIgnoreCase("test"))
                .thenReturn(List.of(testOrder));

        List<Order> result = orderService.searchOrdersByUserEmail("test");

        assertEquals(1, result.size());
        verify(orderRepository).findByUserEmailContainingIgnoreCase("test");
    }

    @Test
    void deleteOrder_alreadyCancelled_noAction() {
        testOrder.setStatus(OrderStatus.ANNULEE);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        orderService.deleteOrder(1L);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void deleteOrder_activeOrder_restoresStock() {
        Product product = new Product();
        product.setId(1L);
        product.setStock(10);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);
        testOrder.setItems(Set.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any())).thenReturn(testOrder);
        when(productRepository.save(any())).thenReturn(product);

        orderService.deleteOrder(1L);

        assertEquals(OrderStatus.ANNULEE, testOrder.getStatus());
        assertEquals(12, product.getStock());
        verify(productRepository).save(product);
        verify(orderRepository).save(testOrder);
    }

    @Test
    void updateStatus_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        Order result = orderService.updateStatus(1L, OrderStatus.VALIDEE);

        assertEquals(OrderStatus.VALIDEE, result.getStatus());
    }

    @Test
    void calculateOrderTotal_withItems() {
        Product product = new Product();
        product.setPrix(10.0);

        OrderItem item = new OrderItem();
        item.setQuantity(3);
        item.setProduct(product);

        testOrder.setItems(Set.of(item));

        double total = orderService.calculateOrderTotal(testOrder);

        assertEquals(30.0, total, 0.01);
    }

    @Test
    void calculateOrderTotal_emptyItems() {
        testOrder.setItems(Set.of());

        double total = orderService.calculateOrderTotal(testOrder);

        assertEquals(0.0, total, 0.01);
    }

    @Test
    void getOrderStatus_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        OrderStatus status = orderService.getOrderStatus(1L);

        assertEquals(OrderStatus.EN_COURS, status);
    }
}

