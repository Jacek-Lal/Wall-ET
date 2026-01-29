package com.wallet.instrument_service.core.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InstrumentRequest(
        @NotBlank String ticker,
        @NotBlank String name,
        @NotBlank String exchange,
        @NotBlank @Size(min = 2, max = 2) String country,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotBlank String market,
        String asset_type,
        @Size(max = 10) @Pattern(regexp = "^[0-9]*$") String cik
) {}
