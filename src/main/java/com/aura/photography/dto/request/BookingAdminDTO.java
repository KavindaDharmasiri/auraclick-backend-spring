package com.aura.photography.dto.request;

import com.aura.photography.util.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * @Author: kasun_t
 * @Package: com.aura.photography.dto.request
 * @Class: BookingAdminDTO
 * @Created on: 2/2/2026 at 2:26 PM
 */
@Data
@Schema(description = "DTO for Booking Admin Filters")
public class BookingAdminDTO {
    @Schema(description = "Type of the booking", example = "WEDDING")
    private String bookingType;
    @Schema(description = "From date for the booking filter", example = "2026-01-01")
    private LocalDate fromDate;
    @Schema(description = "To date for the booking filter", example = "2026-12-31")
    private LocalDate toDate;
    @Schema(description = "Status of the booking", example = "CONFIRMED")
    private BookingStatus bookingStatus;

    //pagination fields
    @Schema(description = "Page number for pagination", example = "0")
    private int pageNumber = 0;
    @Schema(description = "Page size for pagination", example = "10")
    private int pageSize = 10;
}
