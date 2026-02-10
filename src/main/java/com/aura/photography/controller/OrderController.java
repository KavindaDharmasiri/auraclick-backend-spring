package com.aura.photography.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.aura.photography.dto.response.BookingDTO;
import com.aura.photography.dto.response.OrderDTO;
import com.aura.photography.dto.response.PhotoshootBookingDTO;
import com.aura.photography.dto.response.StudioBookingDTO;
import com.aura.photography.model.*;
import com.aura.photography.repository.*;
import com.aura.photography.util.enums.PaymentStatus;
import com.aura.photography.util.enums.PaymentType;
import com.aura.photography.util.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private PhotoshootBookingRepository photoshootBookingRepository;
    
    @Autowired
    private StudioBookingRepository studioBookingRepository;
    
    @Autowired
    private PaymentSlipRepository paymentSlipRepository;
    
    @Value("${file.upload-dir:uploads/payment-slips}")
    private String uploadDir;
    
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
            List<Map<String, Object>> ordersWithSlips = new ArrayList<>();
            
            for (Order order : orders) {
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("id", order.getId());
                orderData.put("orderNumber", order.getOrderNumber());
                orderData.put("orderDate", order.getOrderDate());
                orderData.put("status", order.getStatus());
                orderData.put("totalAmount", order.getTotalAmount());
                orderData.put("user", Map.of(
                    "id", order.getUser().getId(),
                    "firstName", order.getUser().getFirstName(),
                    "lastName", order.getUser().getLastName(),
                    "email", order.getUser().getEmail()
                ));
                
                // Check if payment slip exists
                PaymentSlip slip = paymentSlipRepository.findByOrder(order).orElse(null);
                if (slip != null) {
                    try {
                        Path filePath = Paths.get(slip.getFilePath());
                        byte[] fileBytes = Files.readAllBytes(filePath);
                        String base64 = java.util.Base64.getEncoder().encodeToString(fileBytes);
                        orderData.put("paymentSlip", Map.of(
                            "fileName", slip.getFileName(),
                            "fileBase64", base64,
                            "uploadedAt", slip.getUploadedAt()
                        ));
                    } catch (Exception e) {
                        orderData.put("paymentSlip", null);
                    }
                } else {
                    orderData.put("paymentSlip", null);
                }
                
                ordersWithSlips.add(orderData);
            }
            
            return ResponseEntity.ok(ordersWithSlips);
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
            
            double totalAmount = Double.parseDouble(orderData.get("totalAmount").toString());
            double subtotal = Double.parseDouble(orderData.get("subtotal").toString());
            double serviceFee = Double.parseDouble(orderData.get("serviceFee").toString());
            double tax = Double.parseDouble(orderData.get("tax").toString());
            String paymentMethod = orderData.get("paymentMethod").toString();
            
            Payment payment = new Payment();
            payment.setUser(user);
            payment.setAmount(totalAmount);
            payment.setPaymentType(PaymentType.CARD);
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setPaymentDate(LocalDateTime.now());
            
            if (orderData.containsKey("cardDetails") && orderData.get("cardDetails") != null) {
                Map<String, Object> cardDetails = (Map<String, Object>) orderData.get("cardDetails");
                payment.setCardLast4(cardDetails.get("last4").toString());
                payment.setCardType(cardDetails.get("cardType").toString());
            }
            
            Payment savedPayment = paymentRepository.save(payment);
            
            Order order = new Order();
            order.setUser(user);
            order.setPayment(savedPayment);
            order.setOrderNumber("ORD-" + System.currentTimeMillis());
            order.setTotalAmount(totalAmount);
            order.setSubtotal(subtotal);
            order.setServiceFee(serviceFee);
            order.setTax(tax);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(paymentMethod.equals("slip") ? "PENDING" : "PAID");
            
            Order savedOrder = orderRepository.save(order);
            
            List<Cart> userCartItems = cartRepository.findByUser(user);
            List<OrderItem> orderItems = new ArrayList<>();
            
            for (Cart cartItem : userCartItems) {
                Gear gear = cartItem.getGear();
                OrderItem orderItem = new OrderItem(
                    savedOrder,
                    gear,
                    cartItem.getQuantity(),
                    cartItem.getDuration(),
                    gear.getRentalPrice(),
                    cartItem.getTotalPrice()
                );
                orderItems.add(orderItem);
                
                int newStock = gear.getStock() - cartItem.getQuantity();
                if (newStock < 0) newStock = 0;
                gear.setStock(newStock);
                if (newStock == 0) gear.setStatus("Out of Stock");
                gearRepository.save(gear);
            }
            
            orderItemRepository.saveAll(orderItems);
            savedOrder.setOrderItems(orderItems);
            cartRepository.deleteAll(userCartItems);
            
            // Handle base64 file upload if provided
            if (orderData.containsKey("fileBase64") && orderData.get("fileBase64") != null) {
                try {
                    String fileBase64 = orderData.get("fileBase64").toString();
                    String fileName = orderData.get("fileName").toString();
                    
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    
                    String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                    String uniqueFilename = "slip_" + savedOrder.getId() + "_" + System.currentTimeMillis() + fileExtension;
                    Path filePath = uploadPath.resolve(uniqueFilename);
                    
                    byte[] fileBytes = java.util.Base64.getDecoder().decode(fileBase64);
                    Files.write(filePath, fileBytes);
                    
                    PaymentSlip paymentSlip = new PaymentSlip(savedOrder, filePath.toString(), fileName);
                    paymentSlipRepository.save(paymentSlip);
                } catch (Exception e) {
                    System.err.println("Failed to upload payment slip: " + e.getMessage());
                }
            }
            
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
    
    @GetMapping("/bookings")
    public ResponseEntity<?> getUserBookings(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            
            Map<String, Object> allBookings = new HashMap<>();
            
            // Get general bookings and convert to DTO
            var generalBookings = bookingRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(booking -> new BookingDTO(
                    booking.getId(),
                    booking.getService() != null ? booking.getService().getName() : "General Service",
                    booking.getStatus().toString(),
                    booking.getPaymentStatus().toString(),
                    booking.getBookingDate(),
                    booking.getCreatedAt(),
                    booking.getPayment() != null ? booking.getPayment().getAmount() : 0.0
                ))
                .collect(Collectors.toList());
            allBookings.put("general", generalBookings);
            
            // Get photoshoot bookings and convert to DTO
            var photoshootBookings = photoshootBookingRepository.findByBooking_UserOrderByBooking_CreatedAtDesc(user)
                .stream()
                .map(psBooking -> new PhotoshootBookingDTO(
                    psBooking.getId(),
                    psBooking.getBooking().getStatus().toString(),
                    psBooking.getBooking().getPaymentStatus().toString(),
                    psBooking.getBooking().getBookingDate(),
                    psBooking.getBooking().getCreatedAt(),
                    psBooking.getLocation(),
                    psBooking.getDuration() != null ? psBooking.getDuration().toString() : "N/A",
                    psBooking.getBooking().getPayment() != null ? psBooking.getBooking().getPayment().getAmount() : 0.0
                ))
                .collect(Collectors.toList());
            allBookings.put("photoshoot", photoshootBookings);
            
            // Get studio bookings and convert to DTO
            var studioBookings = studioBookingRepository.findByBooking_UserOrderByBooking_CreatedAtDesc(user)
                .stream()
                .map(studioBooking -> new StudioBookingDTO(
                    studioBooking.getId(),
                    studioBooking.getBooking().getStatus().toString(),
                    studioBooking.getBooking().getPaymentStatus().toString(),
                    studioBooking.getBooking().getBookingDate(),
                    studioBooking.getBooking().getCreatedAt(),
                    getStudioName(studioBooking.getStudioId()),
                    getTimeSlotNames(studioBooking.getTimeSlot()),
                    studioBooking.getBooking().getPayment() != null ? studioBooking.getBooking().getPayment().getAmount() : 0.0
                ))
                .collect(Collectors.toList());
            allBookings.put("studio", studioBookings);
            
            return ResponseEntity.ok(allBookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to retrieve bookings");
        }
    }
    
    private String getStudioName(int studioId) {
        switch (studioId) {
            case 1: return "Main Hall";
            case 2: return "Cyc Wall";
            case 3: return "Boudoir Suite";
            default: return "Studio " + studioId;
        }
    }
    
    private List<String> getTimeSlotNames(String timeSlotString) {
        if (timeSlotString == null || timeSlotString.isEmpty()) return List.of();
        
        try {
            // Parse comma-separated string like "1,2,3" to List<Integer>
            List<Integer> timeSlotIds = List.of(timeSlotString.split(","))
                .stream()
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
            
            Map<Integer, String> timeSlotMap = Map.of(
                1, "09:00 - 11:00",
                2, "11:00 - 13:00",
                3, "13:00 - 15:00",
                4, "15:00 - 17:00",
                5, "17:00 - 19:00",
                6, "19:00 - 21:00"
            );
            
            return timeSlotIds.stream()
                .map(id -> timeSlotMap.getOrDefault(id, "Unknown"))
                .collect(Collectors.toList());
        } catch (Exception e) {
            // If parsing fails, return the original string as single item
            return List.of(timeSlotString);
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> statusData, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail).orElse(null);

            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ResponseEntity.badRequest().body("Order not found");
            }
            
            String newStatus = statusData.get("status");
            order.setStatus(newStatus);
            orderRepository.save(order);

            return ResponseEntity.ok(Map.of("message", "Order status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update order status: " + e.getMessage());
        }
    }
    
    @PostMapping("/{orderId}/upload-slip")
    public ResponseEntity<?> uploadPaymentSlip(@PathVariable Long orderId, @RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ResponseEntity.badRequest().body("Order not found");
            }
            
            if (!order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Access denied");
            }
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            
            // Create upload directory if not exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = "slip_" + orderId + "_" + System.currentTimeMillis() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);
            
            // Save file
            Files.copy(file.getInputStream(), filePath);
            
            // Save payment slip record
            PaymentSlip paymentSlip = new PaymentSlip(order, filePath.toString(), originalFilename);
            paymentSlipRepository.save(paymentSlip);
            
            return ResponseEntity.ok(Map.of(
                "message", "Payment slip uploaded successfully",
                "fileName", uniqueFilename
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload payment slip: " + e.getMessage());
        }
    }
    
    @GetMapping("/{orderId}/paymentSlip")
    public ResponseEntity<?> getPaymentSlip(@PathVariable Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            
            PaymentSlip slip = paymentSlipRepository.findByOrder(order)
                    .orElseThrow(() -> new IllegalArgumentException("Payment slip not found"));
            
            Path filePath = Paths.get(slip.getFilePath());
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            String contentType = "application/octet-stream";
            String filename = slip.getFileName();
            if (filename != null) {
                if (filename.toLowerCase().endsWith(".pdf")) contentType = "application/pdf";
                else if (filename.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)")) contentType = "image/" + filename.substring(filename.lastIndexOf('.') + 1);
            }
            
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Failed to retrieve payment slip: " + ex.getMessage());
        }
    }
}
