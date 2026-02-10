package com.aura.photography.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: kasun
 * @Package: com.aura.photography.util.enums
 * @Enum: PaymentStatus
 * @Created on: 1/31/2026 at 10:16 AM
 */
@Getter
@AllArgsConstructor
public enum PaymentStatus {

    PAID("PAID", "Paid"),
    PENDING("PENDING", "Pending"),
    PARTIALLY_PAID("PARTIALLY_PAID", "Partial"),
    UNPAID("UNPAID", "Unpaid"), COMPLETED("COMPLETED","Completed" );

    private final String code;
    private final String description;
}
