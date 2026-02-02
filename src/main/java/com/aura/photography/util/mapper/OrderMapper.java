package com.aura.photography.util.mapper;

import com.aura.photography.dto.response.*;
import com.aura.photography.model.*;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    
    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;
        
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setSubtotal(order.getSubtotal());
        dto.setServiceFee(order.getServiceFee());
        dto.setTax(order.getTax());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        
        if (order.getUser() != null) {
            dto.setUser(toUserDTO(order.getUser()));
        }
        
        if (order.getPayment() != null) {
            dto.setPayment(toPaymentDTO(order.getPayment()));
        }
        
        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems().stream()
                .map(OrderMapper::toOrderItemDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public static List<OrderDTO> toDTOList(List<Order> orders) {
        return orders.stream()
            .map(OrderMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    private static UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
    }
    
    private static PaymentDTO toPaymentDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymentType().toString());
        dto.setPaymentStatus(payment.getPaymentStatus().toString());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setCardType(payment.getCardType());
        dto.setCardLast4(payment.getCardLast4());
        return dto;
    }
    
    private static OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setDuration(orderItem.getDuration());
        dto.setUnitPrice(orderItem.getUnitPrice());
        dto.setTotalPrice(orderItem.getTotalPrice());
        
        if (orderItem.getGear() != null) {
            dto.setGear(toGearDTO(orderItem.getGear()));
        }
        
        return dto;
    }
    
    private static GearDTO toGearDTO(Gear gear) {
        GearDTO dto = new GearDTO();
        dto.setId(gear.getId());
        dto.setName(gear.getName());
        dto.setSku(gear.getSku());
        dto.setBrand(gear.getBrand());
        dto.setModel(gear.getModel());
        dto.setCategory(gear.getCategory());
        dto.setDescription(gear.getDescription());
        dto.setStock(gear.getStock());
        dto.setRentalPrice(gear.getRentalPrice());
        dto.setStatus(gear.getStatus());
        dto.setCondition(gear.getCondition());
        dto.setImages(gear.getImages());
        return dto;
    }
}