package com.aura.photography.dto.request;

import com.aura.photography.util.enums.PaymentStatus;
import com.aura.photography.util.enums.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @Author: kasun
 * @Package: com.aura.photography.dto.request
 * @Class: StudioDTO
 * @Created on: 2/1/2026 at 9:12 PM
 */
@Data
@Schema(description = "Request DTO for booking a studio")
public class StudioDTO {
    @Schema(description = "Is full payment made", example = "true")
    private Boolean fullPayment;
    @Schema(description = "Payment Type", example = "CASH")
    private PaymentType paymentType;
    @Schema(description = "Payment Amount", example = "150.00")
    private Double paymentAmount;
    @Schema(description = "Booking Date", example = "2026-06-20")
    private LocalDate bookingDate;
    @Schema(description = "Studio ID", example = "2")
    private int StudioId;
    @Schema(description = "Time Slots", example = "[1, 2, 3]")
    private Set<Integer> timeSlot;
    @Schema(description = "User who created the booking (Set automatically)", hidden = true)
    private String createdBy;
}
