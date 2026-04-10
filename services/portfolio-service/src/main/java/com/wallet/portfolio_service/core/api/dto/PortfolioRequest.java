package com.wallet.portfolio_service.core.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PortfolioRequest(@NotBlank String name,
                               @NotBlank @Pattern(regexp = "^[a-zA-Z0-9]{3}$") String baseCurrency) {
}
