package com.aura.photography.controller;

import com.aura.photography.dto.response.OrderDTO;
import com.aura.photography.model.*;
import com.aura.photography.repository.*;
import com.aura.photography.util.enums.PaymentStatus;
import com.aura.photography.util.enums.PaymentType;
import com.aura.photography.util.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private GearRepository gearRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
            List<OrderDTO> orderDTOs = OrderMapper.toDTOList(orders);
            return ResponseEntity.ok(orderDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve orders: " + e.getMessage());
        }
    }
    
    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            
            List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
            List<OrderDTO> orderDTOs = OrderMapper.toDTOList(orders);
            return ResponseEntity.ok(orderDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve orders: " + e.getMessage());
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderData, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            
            // Extract order data
            double totalAmount = Double.parseDouble(orderData.get("totalAmount").toString());
            double subtotal = Double.parseDouble(orderData.get("subtotal").toString());
            double serviceFee = Double.parseDouble(orderData.get("serviceFee").toString());
            double tax = Double.parseDouble(orderData.get("tax").toString());
            String paymentMethod = orderData.get("paymentMethod").toString();
            
            // Create payment record
            Payment payment = new Payment();
            payment.setUser(user);
            payment.setAmount(totalAmount);
            payment.setPaymentType(PaymentType.CARD); // Default to card for now
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setPaymentDate(LocalDateTime.now());
            
            // Add card details if available
            if (orderData.containsKey("cardDetails")) {
                Map<String, Object> cardDetails = (Map<String, Object>) orderData.get("cardDetails");
                payment.setCardLast4(cardDetails.get("last4").toString());
                payment.setCardType(cardDetails.get("cardType").toString());
            }
            
            Payment savedPayment = paymentRepository.save(payment);
            
            // Create Order
            Order order = new Order();
            order.setUser(user);
            order.setPayment(savedPayment);
            order.setOrderNumber("ORD-" + System.currentTimeMillis());
            order.setTotalAmount(totalAmount);
            order.setSubtotal(subtotal);
            order.setServiceFee(serviceFee);
            order.setTax(tax);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PAID");
            
            Order savedOrder = orderRepository.save(order);
            
            // Create Order Items and update gear stock
            List<Cart> userCartItems = cartRepository.findByUser(user);
            List<OrderItem> orderItems = new ArrayList<>();
            
            for (Cart cartItem : userCartItems) {
                Gear gear = cartItem.getGear();
                
                // Create order item
                OrderItem orderItem = new OrderItem(
                    savedOrder,
                    gear,
                    cartItem.getQuantity(),
                    cartItem.getDuration(),
                    gear.getRentalPrice(),
                    cartItem.getTotalPrice()
                );
                orderItems.add(orderItem);
                
                // Update gear stock
                int newStock = gear.getStock() - cartItem.getQuantity();
                if (newStock < 0) newStock = 0;
                gear.setStock(newStock);
                if (newStock == 0) gear.setStatus("Out of Stock");
                gearRepository.save(gear);
            }
            
            // Save all order items
            orderItemRepository.saveAll(orderItems);
            savedOrder.setOrderItems(orderItems);
            
            // Clear user's cart
            cartRepository.deleteAll(userCartItems);
            
            return ResponseEntity.ok(Map.of(
                "message", "Order created successfully",
                "orderId", savedOrder.getId(),
                "orderNumber", savedOrder.getOrderNumber(),
                "transactionId", savedPayment.getTransactionId()
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to create order: " + e.getMessage());
        }
    }
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> statusData, Authentication authentication) {
        try {
            System.out.println("11");
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail).orElse(null);
//            if (user == null || !"ADMIN".equals(user.getRole())) {
//                return ResponseEntity.status(403).body("Access denied");
//            }
            
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ResponseEntity.badRequest().body("Order not found");
            }
            System.out.println(statusData.get("status"));
            String newStatus = statusData.get("status");
            order.setStatus(newStatus);
            System.out.println("22");
            orderRepository.save(order);
            
            return ResponseEntity.ok(Map.of("message", "Order status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update order status: " + e.getMessage());
        }
    }
}
