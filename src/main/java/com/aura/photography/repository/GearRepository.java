package com.aura.photography.repository;

import com.aura.photography.model.Gear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GearRepository extends JpaRepository<Gear, Long> {
    List<Gear> findByCategory(String category);
    @Query("SELECT g FROM Gear g WHERE " +
           "(:category IS NULL OR g.category = :category) AND " +
           "(:brands IS NULL OR g.brand IN :brands) AND " +
           "(:minPrice IS NULL OR g.rentalPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR g.rentalPrice <= :maxPrice) AND " +
           "(:status IS NULL OR g.status = :status)")
    Page<Gear> findByFilters(@Param("category") String category,
                            @Param("brands") List<String> brands,
                            @Param("minPrice") Double minPrice,
                            @Param("maxPrice") Double maxPrice,
                            @Param("status") String status,
                            Pageable pageable);
    List<Gear> findByNameContainingIgnoreCase(String name);
    boolean existsBySku(String sku);
    
    // Pagination methods
    Page<Gear> findByCategory(String category, Pageable pageable);
    Page<Gear> findByStatus(String status, Pageable pageable);
    Page<Gear> findByCategoryAndStatus(String category, String status, Pageable pageable);
    Page<Gear> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String name, String sku, Pageable pageable);
}