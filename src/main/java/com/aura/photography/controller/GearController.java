package com.aura.photography.controller;

import com.aura.photography.dto.request.CreateGearDTO;
import com.aura.photography.model.Gear;
import com.aura.photography.repository.GearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gear")
@CrossOrigin(origins = "http://localhost:3000")
public class GearController {

    @Autowired
    private GearRepository gearRepository;

    private final String UPLOAD_DIR = "uploads/gear/";

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllGear(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Gear> gearPage;

        if (search != null && !search.isEmpty()) {
            gearPage = gearRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(search, search, pageable);
        } else if (category != null && status != null) {
            gearPage = gearRepository.findByCategoryAndStatus(category, status, pageable);
        } else if (category != null) {
            gearPage = gearRepository.findByCategory(category, pageable);
        } else if (status != null) {
            gearPage = gearRepository.findByStatus(status, pageable);
        } else {
            gearPage = gearRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", gearPage.getContent());
        response.put("totalElements", gearPage.getTotalElements());
        response.put("totalPages", gearPage.getTotalPages());
        response.put("currentPage", gearPage.getNumber());
        response.put("size", gearPage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gear> getGearById(@PathVariable Long id) {
        Optional<Gear> gear = gearRepository.findById(id);
        return gear.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGear(@RequestBody CreateGearDTO gearDTO) {
        if (gearRepository.existsBySku(gearDTO.getSku())) {
            return ResponseEntity.badRequest().body("SKU already exists");
        }

        Gear gear = new Gear();
        gear.setName(gearDTO.getName());
        gear.setSku(gearDTO.getSku());
        gear.setBrand(gearDTO.getBrand());
        gear.setModel(gearDTO.getModel());
        gear.setSerialNumber(gearDTO.getSerialNumber());
        gear.setCategory(gearDTO.getCategory());
        gear.setDescription(gearDTO.getDescription());
        gear.setStock(gearDTO.getStock());
        gear.setTotalStock(gearDTO.getTotalStock());
        gear.setRentalPrice(gearDTO.getRentalPrice());
        gear.setStatus(gearDTO.getStatus());
        gear.setCondition(gearDTO.getCondition());
        gear.setImages(gearDTO.getImageUrls());

        Gear savedGear = gearRepository.save(gear);
        return ResponseEntity.ok(savedGear);
    }

    @PostMapping
    public ResponseEntity<?> createGear(
            @RequestParam("name") String name,
            @RequestParam("sku") String sku,
            @RequestParam("brand") String brand,
            @RequestParam("model") String model,
            @RequestParam(value = "serialNumber", required = false) String serialNumber,
            @RequestParam("category") String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("stock") Integer stock,
            @RequestParam("totalStock") Integer totalStock,
            @RequestParam("rentalPrice") Double rentalPrice,
            @RequestParam("status") String status,
            @RequestParam("condition") String condition,
            @RequestParam(value = "purchaseDate", required = false) String purchaseDate,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {

        if (gearRepository.existsBySku(sku)) {
            return ResponseEntity.badRequest().body("SKU already exists");
        }

        Gear gear = new Gear();
        gear.setName(name);
        gear.setSku(sku);
        gear.setBrand(brand);
        gear.setModel(model);
        gear.setSerialNumber(serialNumber);
        gear.setCategory(category);
        gear.setDescription(description);
        gear.setStock(stock);
        gear.setTotalStock(totalStock);
        gear.setRentalPrice(rentalPrice);
        gear.setStatus(status);
        gear.setCondition(condition);

        if (purchaseDate != null && !purchaseDate.isEmpty()) {
            gear.setPurchaseDate(java.time.LocalDate.parse(purchaseDate));
        }

        // Handle image uploads
        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                        Path uploadPath = Paths.get(UPLOAD_DIR);
                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }
                        Files.copy(image.getInputStream(), uploadPath.resolve(filename));
                        imageUrls.add("/uploads/gear/" + filename);
                    } catch (IOException e) {
                        return ResponseEntity.badRequest().body("Failed to upload image: " + e.getMessage());
                    }
                }
            }
        }
        gear.setImages(imageUrls);

        Gear savedGear = gearRepository.save(gear);
        return ResponseEntity.ok(savedGear);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gear> updateGear(@PathVariable Long id, @RequestBody Gear gearDetails) {
        Optional<Gear> optionalGear = gearRepository.findById(id);
        if (!optionalGear.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Gear gear = optionalGear.get();
        gear.setName(gearDetails.getName());
        gear.setBrand(gearDetails.getBrand());
        gear.setModel(gearDetails.getModel());
        gear.setSerialNumber(gearDetails.getSerialNumber());
        gear.setCategory(gearDetails.getCategory());
        gear.setDescription(gearDetails.getDescription());
        gear.setStock(gearDetails.getStock());
        gear.setTotalStock(gearDetails.getTotalStock());
        gear.setRentalPrice(gearDetails.getRentalPrice());
        gear.setStatus(gearDetails.getStatus());
        gear.setCondition(gearDetails.getCondition());
        gear.setPurchaseDate(gearDetails.getPurchaseDate());

        Gear updatedGear = gearRepository.save(gear);
        return ResponseEntity.ok(updatedGear);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGear(@PathVariable Long id) {
        if (!gearRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        gearRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public List<Gear> searchGear(@RequestParam String query) {
        return gearRepository.findByNameContainingIgnoreCase(query);
    }

    @GetMapping("/category/{category}")
    public List<Gear> getGearByCategory(@PathVariable String category) {
        return gearRepository.findByCategory(category);
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterGear(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String status) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Gear> gearPage;

        if (category != null || (brands != null && !brands.isEmpty()) || minPrice != null || maxPrice != null || status != null) {
            gearPage = gearRepository.findByFilters(category, brands, minPrice, maxPrice, status, pageable);
        } else {
            gearPage = gearRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", gearPage.getContent());
        response.put("totalElements", gearPage.getTotalElements());
        response.put("totalPages", gearPage.getTotalPages());
        response.put("currentPage", gearPage.getNumber());
        response.put("size", gearPage.getSize());

        return ResponseEntity.ok(response);
    }
}
