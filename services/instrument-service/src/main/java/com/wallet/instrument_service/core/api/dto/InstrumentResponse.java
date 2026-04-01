package com.wallet.instrument_service.core.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InstrumentResponse(Long id, String ticker, String name, Market market, String primaryExchange,
                                 String currencySymbol, String baseCurrencySymbol, InstrumentType type, String cik,
                                 Instant createdAt) {
}
