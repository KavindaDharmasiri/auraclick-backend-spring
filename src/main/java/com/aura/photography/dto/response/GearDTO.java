package com.aura.photography.dto.response;

import java.time.LocalDate;
import java.util.List;

public class GearDTO {
    private Long id;
    private String name;
    private String sku;
    private String brand;
    private String model;
    private String category;
    private String description;
    private Integer stock;
    private Double rentalPrice;
    private String status;
    private String condition;
    private List<String> images;

    // Constructors
    public GearDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Double getRentalPrice() { return rentalPrice; }
    public void setRentalPrice(Double rentalPrice) { this.rentalPrice = rentalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}