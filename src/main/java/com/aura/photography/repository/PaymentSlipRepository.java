package com.aura.photography.repository;

import com.aura.photography.model.Booking;
import com.aura.photography.model.Order;
import com.aura.photography.model.PaymentSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentSlipRepository extends JpaRepository<PaymentSlip, Long> {
    Optional<PaymentSlip> findByOrder(Order order);
    Optional<PaymentSlip> findByBooking(Booking booking);
}
