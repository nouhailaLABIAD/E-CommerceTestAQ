package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.EmptyCartException;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.OrderNotFoundException;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CartService cartService,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
    }

    @Override
    public Order createOrder(User user) {
        // 1. Récupérer le panier
        Cart cart = cartService.getCartByUser(user);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException();
        }

        // 2. Vérifier le stock pour chaque produit
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException(product.getNom(), product.getStock());
            }
        }

        // 3. Créer la commande
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.EN_COURS);
        Order savedOrder = orderRepository.save(order);

        // 4. Créer les OrderItems et décrémenter le stock
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // Décrémenter le stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // Créer l'OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(new HashSet<>(orderItems));

        // 5. Vider le panier
        cartService.clearCart(user);

        return savedOrder;
    }

    @Override
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public OrderStatus getOrderStatus(Long orderId) {
        return getOrderById(orderId).getStatus();
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.EN_COURS) {
            throw new BusinessException("Impossible d'annuler une commande déjà traitée");
        }

        return performSoftCancel(order);
    }

    /**
     * Effectue l'annulation logique d'une commande et restaure le stock.
     * Méthode factorisée utilisée par cancelOrder et deleteOrder.
     *
     * @param order La commande à annuler
     * @return La commande annulée
     */
    private Order performSoftCancel(Order order) {
        // Restaurer le stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.ANNULEE);
        return orderRepository.save(order);
    }

    // ── ADMIN IMPLEMENTATIONS ─────────────────────────────────────────────────

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getAdminOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public List<Order> searchOrdersByUserEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return getAllOrders();
        }
        return orderRepository.findByUserEmailContainingIgnoreCase(email.trim());
    }

    @Override
    public void deleteOrder(Long orderId) {
        Order order = getAdminOrderById(orderId);
        if (order.getStatus() == OrderStatus.ANNULEE) {
            return; // already deleted
        }
        performSoftCancel(order);
    }

    @Override
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = getAdminOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public double calculateOrderTotal(Order order) {
        return order.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrix())
                .sum();
    }
}
