package com.aura.photography.service.booking;

import com.aura.photography.dto.request.BookingAdminDTO;
import com.aura.photography.dto.request.BookingStatusUpdateDTO;
import com.aura.photography.dto.response.CommonResponse;
import org.springframework.http.ResponseEntity;

/**
 * @Author: kasun_t
 * @Package: com.aura.photography.service.booking
 * @Interface: BookingAdminService
 * @Created on: 2/2/2026 at 3:05 PM
 */
public interface BookingAdminService {
    ResponseEntity<CommonResponse> getBookingTable(BookingAdminDTO request);

    ResponseEntity<CommonResponse> getDashboardMetrics();

    ResponseEntity<CommonResponse> updateStatuses(BookingStatusUpdateDTO request);
}
