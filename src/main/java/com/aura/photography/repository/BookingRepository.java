package com.aura.photography.repository;

import com.aura.photography.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: kasun
 * @Package: com.aura.photography.repository
 * @Interface: BookingRepository
 * @Created on: 1/31/2026 at 3:50 PM
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


}
