package com.aura.photography.service.booking;

import com.aura.photography.dto.request.PhotoshootDTO;
import com.aura.photography.dto.response.CommonResponse;
import org.springframework.http.ResponseEntity;

/**
 * @Author: kasun_t
 * @Package: com.aura.photography.service.booking
 * @Interface: PhotoshootService
 * @Created on: 2/2/2026 at 12:07 PM
 */
public interface PhotoshootService {
    ResponseEntity<CommonResponse> setPhotoshootBooking(PhotoshootDTO photoshootDTO);
}
