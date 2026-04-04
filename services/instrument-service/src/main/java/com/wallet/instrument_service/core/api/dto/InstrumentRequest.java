package com.wallet.instrument_service.core.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record InstrumentRequest(

        @NotBlank
        String ticker,

        @NotBlank
        String name,

        @NotNull
        Market market,

        @JsonProperty("primary_exchange")
        String primaryExchange,

        @JsonProperty("currency_symbol")
        @Pattern(regexp = "^[A-Z]{3}$")
        String currencySymbol,

        @JsonProperty("base_currency_symbol")
        String baseCurrencySymbol,

        InstrumentType type,

        @Pattern(regexp = "^\\d{10}$")
        String cik
) {}
