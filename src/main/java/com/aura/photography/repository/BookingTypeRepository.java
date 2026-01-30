package com.aura.photography.repository;

import com.aura.photography.model.BookingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingTypeRepository extends JpaRepository<BookingType, Long> {
    Optional<BookingType> findByName(String name);
    boolean existsByName(String name);
}
