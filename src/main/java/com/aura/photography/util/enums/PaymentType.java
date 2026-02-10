package com.aura.photography.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: kasun
 * @Package: com.aura.photography.util.enums
 * @Enum: PaymentType
 * @Created on: 2/1/2026 at 8:24 PM
 */
@Getter
@AllArgsConstructor
public enum PaymentType {
    CASH("CASH", "Cash"),
    CARD("CARD", "Card"),
    SLIP("SLIP", "Slip"),
    BANK_TRANSFER("BANK_TRANSFER", "Bank Transfer");

    private final String code;
    private final String description;
}
