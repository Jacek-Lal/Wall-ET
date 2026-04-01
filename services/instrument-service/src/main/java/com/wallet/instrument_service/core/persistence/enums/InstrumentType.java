package com.wallet.instrument_service.core.persistence.enums;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum InstrumentType {
    CS, ADRC, ADRP, ADRR, UNIT, RIGHT, PFD, FUND, SP,
    WARRANT, INDEX, ETF, ETN, OS, GDR, OTHER,
    NYRS, AGEN, EQLK, BOND, ADRW, BASKET, LT;

    public static InstrumentType from(String value) {
        if (value == null) return null;

        try {
            return InstrumentType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e){
            log.warn("No matching instrument type in enum: %s".formatted(value));
            return InstrumentType.OTHER;
        }
    }
}