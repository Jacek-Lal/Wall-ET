package com.wallet.instrument_service.core.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TickerResponse(
        String ticker,
        String name,
        String market,
        @JsonProperty("primary_exchange") String primaryExchange,
        String type,
        @JsonProperty("currency_symbol") String currencySymbol,
        @JsonProperty("currency_name") String currencyName,
        @JsonProperty("base_currency_symbol") String baseCurrencySymbol,
        String cik,
        boolean active
) {}
