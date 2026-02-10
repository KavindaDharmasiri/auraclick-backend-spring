package com.aura.photography.controller;

import com.aura.photography.dto.request.StudioDTO;
import com.aura.photography.dto.request.PhotoshootDTO;
import com.aura.photography.dto.request.BookingAdminDTO;
import com.aura.photography.dto.request.BookingStatusUpdateDTO;
import com.aura.photography.dto.request.WeddingDTO;
import com.aura.photography.service.booking.StudioService;
import com.aura.photography.service.booking.PhotoshootService;
import com.aura.photography.service.booking.BookingAdminService;
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
    private final PhotoshootService photoshootService;
    private final BookingAdminService bookingAdminService;

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

    // Photoshoot Booking related endpoints
    @Operation(summary = "Set a photoshoot booking", description = "Create a new photoshoot booking")
    @PostMapping("photoshootBooking/setBooking")
    public ResponseEntity<?> setPhotoshootBooking(@RequestBody PhotoshootDTO photoshootDTO, Principal principal) {
        log.info("BookingController -> setPhotoshootBooking -> (Request Type: POST) -> /v1/bookings/photoshootBooking/setBooking -> Request by User: {}", principal.getName());

        photoshootDTO.setCreatedBy(principal.getName());
        return photoshootService.setPhotoshootBooking(photoshootDTO);
    }

    // Admin booking table endpoint
    @Operation(summary = "Get booking table (admin)", description = "Fetch booking table data using filters and pagination")
    @PostMapping("admin/getBookingTable")
    public ResponseEntity<?> getBookingTable(@RequestBody BookingAdminDTO requestDTO, Principal principal) {
        log.info("BookingController -> getBookingTable -> (Request Type: POST) -> /v1/bookings/admin/getBookingTable -> Request by User: {}", principal != null ? principal.getName() : "anonymous");
        return bookingAdminService.getBookingTable(requestDTO);
    }

    // Admin dashboard metrics
    @Operation(summary = "Get dashboard metrics (admin)", description = "Totals for current month and % change vs previous month")
    @GetMapping("admin/metrics")
    public ResponseEntity<?> getDashboardMetrics(Principal principal) {
        log.info("BookingController -> getDashboardMetrics -> (Request Type: GET) -> /v1/bookings/admin/metrics -> Request by User: {}", principal != null ? principal.getName() : "anonymous");
        return bookingAdminService.getDashboardMetrics();
    }

    // Admin update statuses (bookingStatus, paymentStatus)
    @Operation(summary = "Update booking/payment status (admin)", description = "Dynamically update bookingStatus and/or paymentStatus")
    @PostMapping("admin/updateStatuses")
    public ResponseEntity<?> updateStatuses(@RequestBody BookingStatusUpdateDTO requestDTO, Principal principal) {
        log.info("BookingController -> updateStatuses -> (Request Type: PATCH) -> /v1/bookings/admin/updateStatuses -> Request by User: {}", principal != null ? principal.getName() : "anonymous");
        return bookingAdminService.updateStatuses(requestDTO);
    }

    // Admin view payment slip
    @Operation(summary = "View payment slip (admin)", description = "Download/view payment slip file")
    @GetMapping("admin/paymentSlip/{slipId}")
    public ResponseEntity<?> viewPaymentSlip(@PathVariable Long slipId, Principal principal) {
        log.info("BookingController -> viewPaymentSlip -> (Request Type: GET) -> /v1/bookings/admin/paymentSlip/{} -> Request by User: {}", slipId, principal != null ? principal.getName() : "anonymous");
        return bookingAdminService.getPaymentSlip(slipId);
    }

    // Admin approve payment slip
    @Operation(summary = "Approve payment slip (admin)", description = "Approve payment slip and mark as PAID")
    @PostMapping("admin/paymentSlip/approve/{bookingId}")
    public ResponseEntity<?> approvePaymentSlip(@PathVariable Long bookingId, Principal principal) {
        log.info("BookingController -> approvePaymentSlip -> (Request Type: POST) -> /v1/bookings/admin/paymentSlip/approve/{} -> Request by User: {}", bookingId, principal != null ? principal.getName() : "anonymous");
        return bookingAdminService.approvePaymentSlip(bookingId);
    }

    // Admin reject payment slip
    @Operation(summary = "Reject payment slip (admin)", description = "Reject payment slip and mark as UNPAID")
    @PostMapping("admin/paymentSlip/reject/{bookingId}")
    public ResponseEntity<?> rejectPaymentSlip(@PathVariable Long bookingId, Principal principal) {
        log.info("BookingController -> rejectPaymentSlip -> (Request Type: POST) -> /v1/bookings/admin/paymentSlip/reject/{} -> Request by User: {}", bookingId, principal != null ? principal.getName() : "anonymous");
        return bookingAdminService.rejectPaymentSlip(bookingId);
    }
}
