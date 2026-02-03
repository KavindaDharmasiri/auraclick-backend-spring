package com.aura.photography.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class StudioBookingDTO {
    private Long id;
    private String status;
    private String paymentStatus;
    private LocalDate bookingDate;
    private LocalDateTime createdAt;
    private String studioName;
    private List<String> timeSlots;
    private Double amount;
    
    public StudioBookingDTO() {}
    
    public StudioBookingDTO(Long id, String status, String paymentStatus, LocalDate bookingDate,
                           LocalDateTime createdAt, String studioName, List<String> timeSlots, Double amount) {
        this.id = id;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.bookingDate = bookingDate;
        this.createdAt = createdAt;
        this.studioName = studioName;
        this.timeSlots = timeSlots;
        this.amount = amount;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getStudioName() { return studioName; }
    public void setStudioName(String studioName) { this.studioName = studioName; }
    
    public List<String> getTimeSlots() { return timeSlots; }
    public void setTimeSlots(List<String> timeSlots) { this.timeSlots = timeSlots; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}