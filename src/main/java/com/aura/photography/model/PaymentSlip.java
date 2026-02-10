package com.aura.photography.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_slips")
public class PaymentSlip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    public PaymentSlip() {}
    
    public PaymentSlip(Order order, String filePath, String fileName) {
        this.order = order;
        this.filePath = filePath;
        this.fileName = fileName;
        this.uploadedAt = LocalDateTime.now();
    }
    
    public PaymentSlip(Booking booking, String filePath, String fileName) {
        this.booking = booking;
        this.filePath = filePath;
        this.fileName = fileName;
        this.uploadedAt = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
