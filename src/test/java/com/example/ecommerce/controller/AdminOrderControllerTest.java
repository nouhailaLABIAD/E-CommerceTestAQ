package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminOrderController.class)
class AdminOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private Order createOrderWithUser(Long id) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(OrderStatus.EN_COURS);
        order.setItems(new java.util.HashSet<>());
        User user = new User();
        user.setEmail("client@example.com");
        order.setUser(user);
        return order;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listOrders_returnsView() throws Exception {
        Order order = createOrderWithUser(1L);
        when(orderService.getAllOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("orderService"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchOrders_returnsView() throws Exception {
        Order order = createOrderWithUser(1L);
        when(orderService.searchOrdersByUserEmail("test")).thenReturn(List.of(order));

        mockMvc.perform(post("/admin/orders/search")
                        .param("searchQuery", "test")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-orders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void viewOrder_returnsDetailView() throws Exception {
        Order order = createOrderWithUser(1L);
        when(orderService.getAdminOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/admin/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-detail"))
                .andExpect(model().attribute("order", order));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showStatusForm_returnsForm() throws Exception {
        Order order = createOrderWithUser(1L);
        when(orderService.getAdminOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/admin/orders/status/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-status-form"))
                .andExpect(model().attribute("order", order));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatus_redirects() throws Exception {
        when(orderService.updateStatus(eq(1L), any(OrderStatus.class))).thenReturn(createOrderWithUser(1L));

        mockMvc.perform(post("/admin/orders/status/1")
                        .param("status", "VALIDEE")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/orders/1"));

        verify(orderService).updateStatus(1L, OrderStatus.VALIDEE);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrder_redirects() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(get("/admin/orders/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/orders"));

        verify(orderService).deleteOrder(1L);
    }
}

