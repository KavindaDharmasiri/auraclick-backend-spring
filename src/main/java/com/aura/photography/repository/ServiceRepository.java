package com.aura.photography.repository;

import com.aura.photography.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByName(String name);
    Optional<Service> findByCode(String code);
    boolean existsByName(String name);
}
