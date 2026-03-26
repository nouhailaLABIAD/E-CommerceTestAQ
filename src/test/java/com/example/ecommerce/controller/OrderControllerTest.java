// package com.example.ecommerce.controller;

// import com.example.ecommerce.entity.Order;
// import com.example.ecommerce.entity.OrderStatus;
// import com.example.ecommerce.entity.User;
// import com.example.ecommerce.repository.UserRepository;
// import com.example.ecommerce.service.OrderService;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;

// import java.util.List;

// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.when;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(OrderController.class)
// class OrderControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private OrderService orderService;

//     @MockBean
//     private UserRepository userRepository;

//     @Test
//     @WithMockUser(username = "test@example.com")
//     void createOrder_success_redirectsToConfirmation() throws Exception {
//         // Arrange
//         User user = new User();
//         user.setId(1L);
//         user.setEmail("test@example.com");
//         when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));

//         Order order = new Order();
//         order.setId(1L);
//         order.setUser(user);
//         when(orderService.createOrder(any(User.class))).thenReturn(order);

//         // Act & Assert
//         mockMvc.perform(post("/orders/create")
//                 .with(csrf()))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("orders/1/confirmation"));
//     }

//     @Test
//     @WithMockUser(username = "test@example.com")
//     void createOrder_error_redirectsToCart() throws Exception {
//         // Arrange
//         User user = new User();
//         user.setId(1L);
//         when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));
//         when(orderService.createOrder(any(User.class))).thenThrow(new RuntimeException("Stock insuffisant"));

//         // Act & Assert
//         mockMvc.perform(post("/orders/create")
//                 .with(csrf()))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/cart"));
//     }

//     @Test
// @WithMockUser(username = "test@example.com")
//     void orderHistory_returnsHistoryView() throws Exception {
//         // Arrange
//         User user = new User();
//         user.setId(1L);
//         when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));
//         List<Order> orders = List.of(createOrderWithStatus(OrderStatus.EN_COURS));
//         when(orderService.getOrdersByUser(user)).thenReturn(orders);

//         // Act & Assert
//         mockMvc.perform(get("/orders/history"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("order-history"))
//                 .andExpect(model().attributeExists("orders", "user"));

//     }
    
//     private Order createOrderWithStatus(OrderStatus status) {
//         Order order = new Order();
//         order.setStatus(status);
//         return order;
//     }

//     @Test
//     @WithMockUser(username = "test@example.com")
//     void orderConfirmation_success() throws Exception {
//         // Arrange
//         User user = new User();
//         user.setId(1L);
//         when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));

//         Order order = new Order();
//         order.setId(1L);
//         order.setUser(user);
//         order.setStatus(OrderStatus.EN_COURS);
//         when(orderService.getOrderById(1L)).thenReturn(order);

//         // Act & Assert
//         mockMvc.perform(get("/orders/1/confirmation"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("order-confirmation"))
//                 .andExpect(model().attributeExists("order", "total", "user"));
//     }

//     @Test
//     @WithMockUser(username = "test@example.com")
//     void cancelOrder_success() throws Exception {
//         // Arrange
//         User user = new User();
//         user.setId(1L);
//         when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));

//         Order order = new Order();
//         order.setId(1L);
//         order.setUser(user);
//         when(orderService.getOrderById(1L)).thenReturn(order);
//         when(orderService.cancelOrder(1L)).thenReturn(order);

//         // Act & Assert
//         mockMvc.perform(post("/orders/1/cancel")
//                 .with(csrf()))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/orders/history"));
//     }
// }
