package com.wallet.portfolio_service.core.integration.dto;

public record InstrumentResponse(String ticker, String name, String market,
                                 String currencySymbol, String baseCurrencySymbol, String type) {
}
