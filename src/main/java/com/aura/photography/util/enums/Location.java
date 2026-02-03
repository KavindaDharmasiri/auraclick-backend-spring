package com.aura.photography.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: kasun_t
 * @Package: com.aura.photography.util.enums
 * @Enum: Location
 * @Created on: 2/2/2026 at 11:15 AM
 */
@Getter
@AllArgsConstructor
public enum Location {

    ONSITE("On Site"),
    STUDIO("Studio");

    private final String displayName;
}
