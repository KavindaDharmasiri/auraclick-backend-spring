package com.aura.photography.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: kasun
 * @Package: com.aura.photography.model
 * @Class: StudioBooking
 * @Created on: 2/1/2026 at 8:40 PM
 */
@Entity
@Table(name = "studio_bookings")
@Data
public class StudioBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    private Booking booking;

    @Column(name = "studio_id")
    private int studioId;

    @Column(name = "time_slot")
    private String timeSlot;
}
