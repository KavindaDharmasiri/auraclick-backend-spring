package com.aura.photography.repository;

import com.aura.photography.model.Booking;
import com.aura.photography.model.StudioBooking;
import com.aura.photography.util.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @Author: kasun
 * @Package: com.aura.photography.repository
 * @Interface: StudioBookingRepository
 * @Created on: 2/1/2026 at 9:38 PM
 */
@Repository
public interface StudioBookingRepository extends JpaRepository<StudioBooking, Long> {

    List<StudioBooking> findAllByBooking_BookingDateAndStudioIdAndBooking_StatusNotIn(
            LocalDate bookingDate,
            int studioId,
            List<BookingStatus> statuses
    );

    Optional<StudioBooking> findByBooking_Id(Long bookingId);
}
