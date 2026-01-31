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
 * @Class: Service
 * @Created on: 1/30/2026 at 11:23 PM
 */
@Entity
@Table(name = "services")
@Getter
@Setter
@ToString(exclude = "bookingTypes")
@EqualsAndHashCode(exclude = "bookingTypes")
@NoArgsConstructor
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;

    @ManyToMany(mappedBy = "services")
    private Set<BookingType> bookingTypes = new HashSet<>();

    public Service(String name) {
        this.name = name;
    }

    public Service(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
