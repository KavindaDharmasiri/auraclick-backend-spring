package com.aura.photography.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDTO {
    private Long id;
    private String serviceType;
    private String status;
    private String paymentStatus;
    private LocalDate bookingDate;
    private LocalDateTime createdAt;
    private Double amount;
    
    public BookingDTO() {}
    
    public BookingDTO(Long id, String serviceType, String status, String paymentStatus, 
                     LocalDate bookingDate, LocalDateTime createdAt, Double amount) {
        this.id = id;
        this.serviceType = serviceType;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.bookingDate = bookingDate;
        this.createdAt = createdAt;
        this.amount = amount;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}