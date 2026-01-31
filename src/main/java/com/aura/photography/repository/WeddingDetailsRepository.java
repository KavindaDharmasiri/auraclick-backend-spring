package com.aura.photography.repository;

import com.aura.photography.model.WeddingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: kasun
 * @Package: com.aura.photography.repository
 * @Interface: WeddingDetailsRepository
 * @Created on: 1/31/2026 at 3:52 PM
 */
@Repository
public interface WeddingDetailsRepository extends JpaRepository<WeddingDetails, Long> {
}
