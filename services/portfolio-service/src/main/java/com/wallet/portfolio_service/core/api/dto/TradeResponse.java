package com.wallet.portfolio_service.core.api.dto;

import com.wallet.portfolio_service.core.persistence.entity.TradeType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeResponse(UUID id, String ticker, TradeType tradeType, BigDecimal quantity,
                            BigDecimal price, String tradeCurrency, Instant tradeDate) {
}
