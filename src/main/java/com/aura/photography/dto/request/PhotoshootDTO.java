package com.aura.photography.dto.request;

import com.aura.photography.util.enums.Duration;
import com.aura.photography.util.enums.Location;
import com.aura.photography.util.enums.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * @Author: kasun_t
 * @Package: com.aura.photography.dto.request
 * @Class: PhotoshootDTO
 * @Created on: 2/2/2026 at 11:03 AM
 */
@Data
public class PhotoshootDTO {
    @Schema(description = "Is full payment made", example = "true")
    private Boolean fullPayment;
    @Schema(description = "Payment Type", example = "CASH")
    private PaymentType paymentType;
    @Schema(description = "Payment Amount", example = "150.00")
    private Double paymentAmount;
    @Schema(description = "Booking Date", example = "2026-06-20")
    private LocalDate bookingDate;
    @Schema(description = "Service Type", example = "OS")
    private String serviceType;
    @Schema(description = "Sub Service Type", example = "PORTRAIT")
    private String subService;
    @Schema(description = "Duration", example = "H2")
    private Duration duration;
    @Schema(description = "Locations", example = "STUDIO")
    private Location location;
    @Schema(description = "User who created the booking (Set automatically)", hidden = true)
    private String createdBy;
}
