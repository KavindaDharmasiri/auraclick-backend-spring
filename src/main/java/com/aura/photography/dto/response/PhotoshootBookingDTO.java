package com.aura.photography.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PhotoshootBookingDTO {
    private Long id;
    private String status;
    private String paymentStatus;
    private LocalDate bookingDate;
    private LocalDateTime createdAt;
    private String location;
    private String duration;
    private Double amount;
    
    public PhotoshootBookingDTO() {}
    
    public PhotoshootBookingDTO(Long id, String status, String paymentStatus, LocalDate bookingDate,
                               LocalDateTime createdAt, String location, String duration, Double amount) {
        this.id = id;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.bookingDate = bookingDate;
        this.createdAt = createdAt;
        this.location = location;
        this.duration = duration;
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
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}