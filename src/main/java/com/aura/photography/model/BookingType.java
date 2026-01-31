package com.aura.photography.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: kasun
 * @Package: com.aura.photography.model
 * @Class: BookingType
 * @Created on: 1/30/2026 at 11:16 PM
 */
@Entity
@Table(name = "booking_types")
@Getter
@Setter
@ToString(exclude = "services")
@EqualsAndHashCode(exclude = "services")
@NoArgsConstructor
public class BookingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;

    @ManyToMany
    @JoinTable(
        name = "booking_type_services",
        joinColumns = @JoinColumn(name = "booking_type_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Service> services = new HashSet<>();

    public BookingType(String name) {
        this.name = name;
    }

    public BookingType(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
