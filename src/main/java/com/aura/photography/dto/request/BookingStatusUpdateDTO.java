package com.aura.photography.dto.request;

import com.aura.photography.util.enums.BookingStatus;
import com.aura.photography.util.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO for admin to update booking and/or payment status dynamically
 */
@Data
public class BookingStatusUpdateDTO {
    @Schema(description = "Booking ID to update", example = "123", required = true)
    private Long bookingId;

    @Schema(description = "New booking status (optional)", example = "CONFIRMED")
    private BookingStatus bookingStatus;

    @Schema(description = "New payment status (optional)", example = "PAID")
    private PaymentStatus paymentStatus;
}
