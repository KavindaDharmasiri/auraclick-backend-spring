package com.aura.photography.controller;

import com.aura.photography.model.Brand;
import com.aura.photography.model.Category;
import com.aura.photography.model.Status;
import com.aura.photography.repository.BrandRepository;
import com.aura.photography.repository.CategoryRepository;
import com.aura.photography.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "http://localhost:3000")
public class SettingsController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private BrandRepository brandRepository;

    // Categories endpoints
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            return ResponseEntity.badRequest().body("Category already exists");
        }
        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(savedCategory);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Statuses endpoints
    @GetMapping("/statuses")
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    @PostMapping("/statuses")
    public ResponseEntity<?> createStatus(@RequestBody Status status) {
        if (statusRepository.existsByName(status.getName())) {
            return ResponseEntity.badRequest().body("Status already exists");
        }
        Status savedStatus = statusRepository.save(status);
        return ResponseEntity.ok(savedStatus);
    }

    @DeleteMapping("/statuses/{id}")
    public ResponseEntity<?> deleteStatus(@PathVariable Long id) {
        if (!statusRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        statusRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Brands endpoints
    @GetMapping("/brands")
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @GetMapping("/brands/category/{categoryId}")
    public List<Brand> getBrandsByCategory(@PathVariable Long categoryId) {
        return brandRepository.findByCategoryId(categoryId);
    }

    @PostMapping("/brands")
    public ResponseEntity<?> createBrand(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        Long categoryId = Long.valueOf(request.get("categoryId").toString());
        
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (!categoryOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Category not found");
        }
        
        Category category = categoryOpt.get();
        if (brandRepository.existsByNameAndCategory(name, category)) {
            return ResponseEntity.badRequest().body("Brand already exists in this category");
        }
        
        Brand brand = new Brand(name, category);
        Brand savedBrand = brandRepository.save(brand);
        return ResponseEntity.ok(savedBrand);
    }

    // Initialize default data endpoint
    @PostMapping("/initialize")
    public ResponseEntity<?> initializeDefaultData() {
        try {
            // Initialize statuses
            String[] defaultStatuses = {
                "In Stock", "Out on Rent", "Maintenance", "Maintain", 
                "Unavailable", "Pending", "Under Maintenance", "Needs Repair"
            };
            
            for (String statusName : defaultStatuses) {
                if (!statusRepository.existsByName(statusName)) {
                    Status status = new Status(statusName);
                    statusRepository.save(status);
                }
            }
            
            // Initialize categories
            String[] defaultCategories = {
                "Cameras", "Lenses", "Lighting", "Accessories", 
                "Audio", "Tripods", "Memory Cards", "Batteries"
            };
            
            for (String categoryName : defaultCategories) {
                if (!categoryRepository.existsByName(categoryName)) {
                    Category category = new Category(categoryName);
                    categoryRepository.save(category);
                }
            }
            
            return ResponseEntity.ok("Default data initialized successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to initialize data: " + e.getMessage());
        }
    }
}
