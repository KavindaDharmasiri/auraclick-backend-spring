package com.aura.photography.service.booking;

import com.aura.photography.dto.request.BookingDTO;
import com.aura.photography.model.Booking;

/**
 * @Author: kasun
 * @Package: com.aura.photography.service.booking
 * @Interface: BookingService
 * @Created on: 1/31/2026 at 3:49 PM
 */
public interface BookingService {
    Booking createBooking(BookingDTO bookingDTO);
}
