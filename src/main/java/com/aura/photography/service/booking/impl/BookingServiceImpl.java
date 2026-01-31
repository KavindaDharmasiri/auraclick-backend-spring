package com.aura.photography.service.booking.impl;

import com.aura.photography.dto.request.BookingDTO;
import com.aura.photography.model.Booking;
import com.aura.photography.repository.BookingRepository;
import com.aura.photography.repository.ServiceRepository;
import com.aura.photography.repository.UserRepository;
import com.aura.photography.service.booking.BookingService;
import com.aura.photography.util.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: kasun
 * @Package: com.aura.photography.service.booking.impl
 * @Class: BookingServiceImpl
 * @Created on: 1/31/2026 at 3:49 PM
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public Booking createBooking(BookingDTO bookingDTO) {
        try {
            Booking booking = new Booking();
            booking.setUser(userRepository.findByEmail(bookingDTO.getCreatedBy())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + bookingDTO.getCreatedBy())));
            booking.setPaymentStatus(bookingDTO.getPaymentStatus() != null ? bookingDTO.getPaymentStatus() : PaymentStatus.UNPAID);
            booking.setBookingDate(bookingDTO.getBookingDate());
            booking.setService(serviceRepository.findByCode(bookingDTO.getService())
                    .orElseThrow(() -> new IllegalArgumentException("Service not found with code: " + bookingDTO.getService())));

            log.debug("Creating booking: {}", booking);
            return bookingRepository.save(booking);
        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage());
            throw e;
        }
    }
}
