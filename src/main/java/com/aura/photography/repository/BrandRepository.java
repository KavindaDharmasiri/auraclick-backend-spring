package com.aura.photography.repository;

import com.aura.photography.model.Brand;
import com.aura.photography.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findByCategory(Category category);
    List<Brand> findByCategoryId(Long categoryId);
    boolean existsByNameAndCategory(String name, Category category);
}