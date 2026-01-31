package com.aura.photography.dto.request;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: kasun
 * @Package: com.aura.photography.dto.request
 * @Class: WeddingDTO
 * @Created on: 1/31/2026 at 3:12 PM
 */
@Data
public class WeddingDTO {
    private LocalDateTime weddingDate;
    private String email;
    private  String partner1FullName;
    private String partner2FullName;
    private int numberOfGuests;
    private String vision;
    private int packageId;
    private String createdBy;
}
