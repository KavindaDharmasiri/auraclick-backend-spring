package com.aura.photography.service.booking;

import com.aura.photography.dto.request.StudioDTO;
import com.aura.photography.dto.response.CommonResponse;
import org.springframework.http.ResponseEntity;

/**
 * @Author: kasun
 * @Package: com.aura.photography.service.booking
 * @Interface: StudioService
 * @Created on: 2/1/2026 at 9:36 PM
 */
public interface StudioService {
    ResponseEntity<CommonResponse> setStudioBooking(StudioDTO studioDTO);

    ResponseEntity<CommonResponse> getAvailableTimeSlots(int studioId, String bookingDate);
}
