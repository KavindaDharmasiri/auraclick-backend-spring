package com.aura.photography.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: kasun_t
 * @Package: com.aura.photography.util.enums
 * @Enum: Duration
 * @Created on: 2/2/2026 at 11:11 AM
 */
@Getter
@AllArgsConstructor
public enum Duration {
    H2("2H", "2 Hours"),
    H4("4H", "4 Hours"),
    H8("8H", "8 Hours"),
    D1("1D", "1 Day"),
    D2("2D", "2 Days");

    private final String code;
    private final String description;
}
