package com.aura.photography.dto.request;

import com.aura.photography.model.Service;
import com.aura.photography.util.enums.BookingStatus;
import com.aura.photography.util.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: kasun
 * @Package: com.aura.photography.dto.request
 * @Class: BookingDTO
 * @Created on: 1/31/2026 at 4:04 PM
 */
@Data
public class BookingDTO {
    private String service;
    private PaymentStatus paymentStatus;
    private LocalDateTime bookingDate;
    private String createdBy;
}
