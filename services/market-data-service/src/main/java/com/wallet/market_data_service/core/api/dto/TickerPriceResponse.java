package com.wallet.market_data_service.core.api.dto;

import com.wallet.market_data_service.core.persistence.entity.TickerPrice;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TickerPriceResponse(String ticker,
                                  BigDecimal price,
                                  LocalDate date) {

    public TickerPriceResponse(TickerPrice tickerPrice) {
        this(tickerPrice.getId().getTicker(),
                tickerPrice.getClose(),
                tickerPrice.getId().getDay());
    }
}
