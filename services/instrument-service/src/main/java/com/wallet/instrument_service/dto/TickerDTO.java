package com.wallet.instrument_service.dto;

public record TickerDTO(
        String ticker,
        String name,
        String market,
        String primary_exchange,
        String type,
        String currency_name,
        String cik,
        String locale
) {
}
