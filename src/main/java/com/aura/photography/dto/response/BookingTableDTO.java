package com.aura.photography.dto.response;

import com.aura.photography.util.enums.BookingStatus;
import lombok.Data;

/**
 * @Author: kasun_t
 * @Package: com.aura.photography.dto.response
 * @Class: BookingTableDTO
 * @Created on: 2/2/2026 at 2:33 PM
 */
@Data
public class BookingTableDTO {

    private Long bookingId;
    private String bookingDate;
    private String customerName;
    private String service;
    private BookingStatus bookingStatus;
    private String duration;
    private String paymentStatus;
    private Boolean hasPaymentSlip;
    private Long paymentSlipId;
}
