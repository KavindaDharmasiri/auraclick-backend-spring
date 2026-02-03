package com.aura.photography.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: kasun_t
 * @Package: com.aura.photography.util.enums
 * @Enum: TimeSlot
 * @Created on: 2/2/2026 at 4:51 PM
 */
@Getter
@AllArgsConstructor
public enum TimeSlot {
    S1("1", "08:00 AM - 11:00 AM"),
    S2("2", "11:00 AM - 13:00 PM"),
    S3("3", "13:00 PM - 15:00 PM"),
    S4("4", "15:00 PM - 17:00 AM");

    private final String code;
    private final String description;
}
