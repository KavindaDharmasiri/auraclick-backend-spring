package com.aura.photography.repository;

import com.aura.photography.model.Booking;
import com.aura.photography.model.User;
import com.aura.photography.util.enums.BookingStatus;
import com.aura.photography.util.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: kasun
 * @Package: com.aura.photography.repository
 * @Interface: BookingRepository
 * @Created on: 1/31/2026 at 3:50 PM
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    long countByBookingDateBetween(java.time.LocalDate start, java.time.LocalDate end);

    long countByBookingDateBetweenAndStatus(java.time.LocalDate start, java.time.LocalDate end, BookingStatus status);

    @Query("select coalesce(sum(p.amount), 0) from Booking b join b.payment p where b.bookingDate between :start and :end and b.paymentStatus <> com.aura.photography.util.enums.PaymentStatus.UNPAID")
    Double sumRevenueByBookingDateRange(@Param("start") java.time.LocalDate start,
                                        @Param("end") java.time.LocalDate end);
    
    List<Booking> findByUserOrderByCreatedAtDesc(User user);
}
