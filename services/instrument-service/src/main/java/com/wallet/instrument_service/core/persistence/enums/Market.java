package com.wallet.instrument_service.core.persistence.enums;

public enum Market {
    STOCKS, FX, CRYPTO, OTC, INDICES;

    public static Market from(String value){
        return Market.valueOf(value.toUpperCase());
    }
}