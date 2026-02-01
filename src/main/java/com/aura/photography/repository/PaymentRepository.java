package com.aura.photography.repository;

import com.aura.photography.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: kasun
 * @Package: com.aura.photography.repository
 * @Interface: PaymentRepository
 * @Created on: 2/1/2026 at 9:40 PM
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
