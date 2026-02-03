package com.aura.photography.model;

import com.aura.photography.util.enums.Duration;
import com.aura.photography.util.enums.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

/**
 * @Author: kasun_t
 * @Package: com.aura.photography.model
 * @Class: PhotoshootBooking
 * @Created on: 2/2/2026 at 11:23 AM
 */
@Entity
@Table(name = "photoshoot_booking")
@Data
public class PhotoshootBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    private Booking booking;

    private String duration;

    private String location;

    @Column(name = "sub_service")
    private String subService;
}
