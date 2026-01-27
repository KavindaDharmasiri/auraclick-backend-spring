package com.aura.photography.repository;

import com.aura.photography.model.Gear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GearRepository extends JpaRepository<Gear, Long> {
    List<Gear> findByCategory(String category);
    List<Gear> findByStatus(String status);
    List<Gear> findByNameContainingIgnoreCase(String name);
    boolean existsBySku(String sku);
    
    // Pagination methods
    Page<Gear> findByCategory(String category, Pageable pageable);
    Page<Gear> findByStatus(String status, Pageable pageable);
    Page<Gear> findByCategoryAndStatus(String category, String status, Pageable pageable);
    Page<Gear> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String name, String sku, Pageable pageable);
}