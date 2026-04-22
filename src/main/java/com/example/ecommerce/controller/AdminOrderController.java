package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ── Liste toutes les commandes ────────────────────────────────────────────
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("orderService", orderService);
        model.addAttribute("searchQuery", "");
        return "admin-orders";
    }

    // ── Recherche par email ───────────────────────────────────────────────────
    @PostMapping("/search")
    public String searchOrders(@RequestParam("searchQuery") String searchQuery, Model model) {
        model.addAttribute("orders", orderService.searchOrdersByUserEmail(searchQuery));
        model.addAttribute("orderService", orderService);
        model.addAttribute("searchQuery", searchQuery);
        return "admin-orders";
    }

    // ── Voir détails commande ─────────────────────────────────────────────────
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Order order = orderService.getAdminOrderById(id);
        model.addAttribute("orderService", orderService);
        model.addAttribute("order", order);
        return "order-detail";
    }

    // ── Formulaire modification statut ────────────────────────────────────────
    @GetMapping("/status/{id}")
    public String showStatusForm(@PathVariable Long id, Model model) {
        Order order = orderService.getAdminOrderById(id);
        model.addAttribute("order", order);
        return "order-status-form";
    }

    // ── Mettre à jour statut ──────────────────────────────────────────────────
    @PostMapping("/status/{id}")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam("status") OrderStatus status) {
        orderService.updateStatus(id, status);
        return "redirect:/admin/orders/" + id;
    }

    // ── Supprimer (soft delete) ───────────────────────────────────────────────
    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/admin/orders";
    }
}
