package com.aura.photography.repository;

import com.aura.photography.model.PhotoshootBooking;
import com.aura.photography.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @Author: kasun_t
 * @Package: com.aura.photography.repository
 * @Interface: PhotoshootBookingRepository
 * @Created on: 2/2/2026 at 12:05 PM
 */
@Repository
public interface PhotoshootBookingRepository extends JpaRepository<PhotoshootBooking, Long> {
    Optional<PhotoshootBooking> findByBooking_Id(Long bookingId);
    
    List<PhotoshootBooking> findByBooking_UserOrderByBooking_CreatedAtDesc(User user);
}
