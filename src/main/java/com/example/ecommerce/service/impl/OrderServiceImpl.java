package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.OrderItemRepository;
// import com.example.ecommerce.repository.ProductRepository; // À ajouter par Membre 1
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    // Injection à ajouter par Membre 1
    // @Autowired
    // private ProductRepository productRepository;

    @Override
    public Order createOrder(User user) {
        // 1. Vérifier le panier
        Cart cart = cartService.getCartByUser(user);
        
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Le panier est vide");
        }

        // 2. Vérifier le stock pour chaque produit
        // TODO: Dépend de ProductRepository (Membre 1)
        /*
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Stock insuffisant pour: " + product.getNom());
            }
        }
        */

        // 3. Créer la commande
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.EN_COURS);
        
        List<OrderItem> orderItems = new ArrayList<>();
        
        // 4. Créer les OrderItems et mettre à jour le stock
        /*
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            
            // Réduire le stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
            
            // Créer l'OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);
        }
        */

        order.setItems(new java.util.HashSet<>(orderItems));
        Order savedOrder = orderRepository.save(order);

        // 5. Sauvegarder les OrderItems
        if (!orderItems.isEmpty()) {
            orderItemRepository.saveAll(orderItems);
        }

        // 6. Vider le panier
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
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
    }

    @Override
    public OrderStatus getOrderStatus(Long orderId) {
        Order order = getOrderById(orderId);
        return order.getStatus();
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
            throw new RuntimeException("Impossible d'annuler une commande déjà traitée");
        }
        
        // Restaurer le stock
        // TODO: Dépend de ProductRepository (Membre 1)
        /*
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
        */
        
        order.setStatus(OrderStatus.ANNULEE);
        return orderRepository.save(order);
    }
}

