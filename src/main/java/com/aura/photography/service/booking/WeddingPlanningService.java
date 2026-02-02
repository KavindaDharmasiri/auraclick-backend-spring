package com.aura.photography.service.booking;

import com.aura.photography.dto.request.WeddingDTO;
import com.aura.photography.dto.response.CommonResponse;
import org.springframework.http.ResponseEntity;

/**
 * @Author: kasun
 * @Package: com.aura.photography.service.booking
 * @Interface: WeddingPlanningService
 * @Created on: 1/30/2026 at 10:03 PM
 */
public interface WeddingPlanningService {
    ResponseEntity<CommonResponse> setBooking(WeddingDTO weddingDTO);
}
