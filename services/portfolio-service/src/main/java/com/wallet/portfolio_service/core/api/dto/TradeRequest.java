package com.wallet.portfolio_service.core.api.dto;

import com.wallet.portfolio_service.core.persistence.entity.TradeType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record TradeRequest(@NotNull String ticker,
                           @NotNull TradeType tradeType,
                           @NotNull @Positive BigDecimal quantity,
                           @NotNull @Positive BigDecimal price,
                           @NotBlank @Size(min = 3, max = 3) @Pattern(regexp = "^[a-zA-Z0-9]{3}$") String tradeCurrency,
                           @NotNull Instant tradeDate) {
}
