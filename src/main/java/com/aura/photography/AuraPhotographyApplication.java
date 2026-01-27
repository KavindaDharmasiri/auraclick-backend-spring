package com.aura.photography;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.aura.photography.model")
@EnableJpaRepositories("com.aura.photography.repository")
public class AuraPhotographyApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuraPhotographyApplication.class, args);
    }
}