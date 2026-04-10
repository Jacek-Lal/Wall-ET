package com.wallet.portfolio_service.core.api.dto;

import java.time.Instant;
import java.util.UUID;

public record PortfolioResponse(UUID id, String name, String baseCurrency, Instant createdAt) {
}
