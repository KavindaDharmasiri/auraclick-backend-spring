package com.aura.photography.config;

import com.aura.photography.model.Category;
import com.aura.photography.model.Status;
import com.aura.photography.repository.CategoryRepository;
import com.aura.photography.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeStatuses();
        initializeCategories();
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
}