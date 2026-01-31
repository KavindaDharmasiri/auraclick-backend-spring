package com.aura.photography.util.enums;

/**
 * @Author: kasun
 * @Package: com.aura.photography.util.enums
 * @Enum: PaymentStatus
 * @Created on: 1/31/2026 at 10:16 AM
 */
public enum PaymentStatus {

    PAID("PAID", "Paid"),
    PARTIALLY_PAID("PARTIALLY_PAID", "Partial"),
    UNPAID("UNPAID", "Unpaid");


    private String code;
    private String description;

    PaymentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }


}
