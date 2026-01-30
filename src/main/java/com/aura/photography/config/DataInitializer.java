package com.aura.photography.config;

import com.aura.photography.model.BookingType;
import com.aura.photography.model.Category;
import com.aura.photography.model.Service;
import com.aura.photography.model.Status;
import com.aura.photography.repository.BookingTypeRepository;
import com.aura.photography.repository.CategoryRepository;
import com.aura.photography.repository.ServiceRepository;
import com.aura.photography.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookingTypeRepository bookingTypeRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeStatuses();
        initializeCategories();
        initializeBookingTypesAndServices();
    }

    private void initializeStatuses() {
        String[] defaultStatuses = {
            "In Stock",
            "Out on Rent", 
            "Maintenance",
            "Maintain",
            "Unavailable",
            "Pending",
            "Under Maintenance",
            "Needs Repair"
        };

        for (String statusName : defaultStatuses) {
            if (!statusRepository.existsByName(statusName)) {
                Status status = new Status(statusName);
                statusRepository.save(status);
                System.out.println("Created status: " + statusName);
            }
        }
    }

    private void initializeCategories() {
        String[] defaultCategories = {
            "Cameras",
            "Lenses",
            "Lighting",
            "Accessories",
            "Audio",
            "Tripods",
            "Memory Cards",
            "Batteries"
        };

        for (String categoryName : defaultCategories) {
            if (!categoryRepository.existsByName(categoryName)) {
                Category category = new Category(categoryName);
                categoryRepository.save(category);
                System.out.println("Created category: " + categoryName);
            }
        }
    }

    private void initializeBookingTypesAndServices() {
        String[] services = {"Outdoor Portrait", "Studio Session", "Wedding Package", "Street Photography"};
        for (String serviceName : services) {
            if (!serviceRepository.existsByName(serviceName)) {
                Service service = new Service(serviceName);
                serviceRepository.save(service);
                System.out.println("Created service: " + serviceName);
            }
        }

        String[] bookingTypes = {"Weddings", "Studio", "Photoshoots", "Rent"};
        for (String typeName : bookingTypes) {
            if (!bookingTypeRepository.existsByName(typeName)) {
                BookingType bookingType = new BookingType(typeName);
                bookingTypeRepository.save(bookingType);
                System.out.println("Created booking type: " + typeName);
            }
        }

        // Map services to booking types
        mapServiceToBookingType("Weddings", "Wedding Package");
        mapServiceToBookingType("Studio", "Studio Session");
        mapServiceToBookingType("Photoshoots", "Outdoor Portrait");
        mapServiceToBookingType("Photoshoots", "Street Photography");
    }

    private void mapServiceToBookingType(String bookingTypeName, String serviceName) {
        bookingTypeRepository.findByName(bookingTypeName).ifPresent(bookingType -> {
            serviceRepository.findByName(serviceName).ifPresent(service -> {
                if (!bookingType.getServices().contains(service)) {
                    bookingType.getServices().add(service);
                    bookingTypeRepository.save(bookingType);
                    System.out.println("Mapped service '" + serviceName + "' to booking type '" + bookingTypeName + "'");
                }
            });
        });
    }
}