package com.aura.photography.dto.request;

import com.aura.photography.util.enums.PaymentStatus;
import com.aura.photography.util.enums.PaymentType;
import lombok.Data;

import java.time.LocalDate;

/**
 * @Author: kasun
 * @Package: com.aura.photography.dto.request
 * @Class: BookingDTO
 * @Created on: 1/31/2026 at 4:04 PM
 */
@Data
public class BookingDTO {
    private String service;
    private PaymentType paymentType;
    private Double paymentAmount;
    private PaymentStatus paymentStatus;
    private LocalDate bookingDate;
    private String createdBy;
}
