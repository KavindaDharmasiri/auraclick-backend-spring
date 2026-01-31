package com.aura.photography.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * @Author: kasun
 * @Package: com.aura.photography.model
 * @Class: WeddingDetails
 * @Created on: 1/31/2026 at 10:46 AM
 */
@Entity
@Table(name = "wedding_details")
@Data
public class WeddingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    private Booking booking;

    @Column(name = "wedding_date")
    private LocalDateTime weddingDate;

    private String email;
    @Column(name = "partner1_full_name")
    private  String partner1FullName;
    @Column(name = "partner2_full_name")
    private String partner2FullName;
    @Column(name = "number_of_guests")
    private int numberOfGuests;
    @Column(length = 2000)
    private String vision;
    @Column(name = "package_id")
    private int packageId;

}
