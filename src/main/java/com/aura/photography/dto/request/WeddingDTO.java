package com.aura.photography.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request DTO for creating a wedding booking")
public class WeddingDTO {
    @Schema(description = "Date of the wedding", example = "2026-06-15T14:30:00")
    private LocalDateTime weddingDate;

    @Schema(description = "Email of the user", example = "user@example.com")
    private String email;

    @Schema(description = "Full name of the first partner", example = "John Doe")
    private  String partner1FullName;

    @Schema(description = "Full name of the second partner", example = "Jane Doe")
    private String partner2FullName;

    @Schema(description = "Number of guests range", example = "50-100")
    private String numberOfGuestsRange;

    @Schema(description = "Vision or additional details for the wedding", example = "A rustic outdoor wedding")
    private String vision;

    @Schema(description = "ID of the selected package", example = "1")
    private int packageId;

    @Schema(description = "User who created the booking (Set automatically)", hidden = true)
    private String createdBy;
}
