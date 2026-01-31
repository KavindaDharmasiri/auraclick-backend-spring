package com.aura.photography.controller;

import com.aura.photography.dto.request.WeddingDTO;
import com.aura.photography.service.booking.WeddingPlanningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @Author: kasun
 * @Package: com.aura.photography.controller
 * @Class: BookingController
 * @Created on: 1/30/2026 at 9:44 PM
 */
@RestController
@RequestMapping("/api/bookings/")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final WeddingPlanningService weddingPlanningService;

    // Wedding planning related endpoints would go here
    @PostMapping("weddingPlanning/setBooking")
    public ResponseEntity<?> setBooking(@RequestBody WeddingDTO weddingDTO, Principal principal) {
        log.info("BookingController -> setBooking -> (Request Type: POST) -> /v1/bookings/weddingPlanning/setBooking -> Request by User: {}", principal.getName());

        weddingDTO.setCreatedBy(principal.getName());
        return weddingPlanningService.setBooking(weddingDTO);
    }
}
