package com.aura.photography.controller;

import com.aura.photography.service.booking.WeddingPlanningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
