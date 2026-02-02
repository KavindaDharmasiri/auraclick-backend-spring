package com.aura.photography.dto.response;

public class OrderItemDTO {
    private Long id;
    private Integer quantity;
    private String duration;
    private Double unitPrice;
    private Double totalPrice;
    private GearDTO gear;

    // Constructors
    public OrderItemDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public GearDTO getGear() { return gear; }
    public void setGear(GearDTO gear) { this.gear = gear; }
}