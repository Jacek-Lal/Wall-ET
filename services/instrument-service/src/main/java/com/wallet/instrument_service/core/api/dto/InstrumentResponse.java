package com.wallet.instrument_service.core.api.dto;

import com.wallet.instrument_service.core.persistence.entity.Instrument;

import java.time.Instant;

public record InstrumentResponse(Long id, String ticker, String name, String exchange, String country,
                                 String currency, String market, String assetType, String cik, Instant createdAt) {

    public InstrumentResponse(Instrument i){
        this(i.getId(), i.getTicker(), i.getName(), i.getExchange(), i.getCountry(), i.getCurrency(), i.getMarket(),
                i.getAssetType(), i.getCik(), i.getCreatedAt());
    }
}
