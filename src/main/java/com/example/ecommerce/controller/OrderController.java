package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Contrôleur pour la gestion des commandes client.
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    /**
     * Crée une commande à partir du panier courant.
     * Appelé depuis le bouton "Passer la commande" du panier.
     */
    @PostMapping("/create")
    public String createOrder(RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();

        if (user == null) {
            return "redirect:/login?redirect=/cart";
        }

        try {
            Order order = orderService.createOrder(user);
            // Redirige vers la page de confirmation / paiement
            return "redirect:/orders/" + order.getId() + "/confirmation";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    /**
     * Page de confirmation de commande (point d'entrée du paiement).
     */
    @GetMapping("/{id}/confirmation")
    public String orderConfirmation(@PathVariable Long id, Model model) {
        User user = getCurrentUser();

        if (user == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderService.getOrderById(id);

            // Sécurité : la commande doit appartenir à l'utilisateur connecté
            if (!order.getUser().getId().equals(user.getId())) {
                return "redirect:/cart";
            }

            // Calcul du total depuis les items
            double total = order.getItems().stream()
                    .mapToDouble(item -> item.getProduct().getPrix() * item.getQuantity())
                    .sum();

            model.addAttribute("order", order);
            model.addAttribute("total", total);
            model.addAttribute("user", user);

            return "order-confirmation";
        } catch (Exception e) {
            return "redirect:/cart";
        }
    }

    /**
     * Historique des commandes de l'utilisateur.
     */
    @GetMapping("/history")
    public String orderHistory(Model model) {
        User user = getCurrentUser();

        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);

        return "order-history";
    }

    /**
     * Annuler une commande EN_COURS.
     */
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();

        if (user == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderService.getOrderById(id);
            if (!order.getUser().getId().equals(user.getId())) {
                return "redirect:/orders/history";
            }
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("success", "Commande annulée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/orders/history";
    }

    // ── Utilitaire ────────────────────────────────────────────────────────────
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }
}