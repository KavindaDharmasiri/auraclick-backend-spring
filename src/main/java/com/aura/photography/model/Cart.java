package com.aura.photography.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "gear_id")
    private Gear gear;
    
    private int quantity;
    private String duration;
    private double totalPrice;
    
    public Cart() {}
    
    public Cart(User user, Gear gear, int quantity, String duration, double totalPrice) {
        this.user = user;
        this.gear = gear;
        this.quantity = quantity;
        this.duration = duration;
        this.totalPrice = totalPrice;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Gear getGear() { return gear; }
    public void setGear(Gear gear) { this.gear = gear; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}