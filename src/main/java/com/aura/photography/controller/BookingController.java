package com.aura.photography.controller;

import com.aura.photography.dto.request.StudioDTO;
import com.aura.photography.dto.request.WeddingDTO;
import com.aura.photography.service.booking.StudioService;
import com.aura.photography.service.booking.WeddingPlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@Tag(name = "Booking Controller", description = "Endpoints for managing bookings")
public class BookingController {

    private final WeddingPlanningService weddingPlanningService;
    private final StudioService studioService;

    // Wedding planning related endpoints would go here
    @Operation(summary = "Set a wedding booking", description = "Create a new wedding planning booking")
    @PostMapping("weddingPlanning/setBooking")
    public ResponseEntity<?> setBooking(@RequestBody WeddingDTO weddingDTO, Principal principal) {
        log.info("BookingController -> setBooking -> (Request Type: POST) -> /v1/bookings/weddingPlanning/setBooking -> Request by User: {}", principal.getName());

        weddingDTO.setCreatedBy(principal.getName());
        return weddingPlanningService.setBooking(weddingDTO);
    }

    // Studio Booking related endpoints would go here
    @Operation(summary = "Set a studio booking", description = "Create a new studio booking")
    @PostMapping("studioBooking/setBooking")
    public ResponseEntity<?> setStudioBooking(@RequestBody StudioDTO studioDTO, Principal principal) {
        log.info("BookingController -> setStudioBooking -> (Request Type: POST) -> /v1/bookings/studioBooking/setBooking -> Request by User: {}", principal.getName());

        studioDTO.setCreatedBy(principal.getName());
        return studioService.setStudioBooking(studioDTO);
    }

    @GetMapping("studioBooking/getAvailableTimeSlots")
    @Operation(summary = "Get available time slots for a studio on a specific date", description = "Retrieve available time slots for studio booking")
    public ResponseEntity<?> getAvailableTimeSlots(@RequestParam int studioId, @RequestParam String bookingDate, Principal principal) {
        log.info("BookingController -> getAvailableTimeSlots -> (Request Type: GET) -> /v1/bookings/studioBooking/getAvailableTimeSlots -> Request by User: {}", principal.getName());
        return studioService.getAvailableTimeSlots(studioId, bookingDate);
    }
}
