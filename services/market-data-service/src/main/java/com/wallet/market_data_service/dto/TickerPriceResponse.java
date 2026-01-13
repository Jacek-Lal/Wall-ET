package com.wallet.market_data_service.dto;

import com.wallet.market_data_service.model.TickerPrice;

import java.math.BigDecimal;
import java.sql.Date;

public record TickerPriceResponse(String ticker,
                                  BigDecimal price,
                                  String currency,
                                  Date date,
                                  String source) {

    public TickerPriceResponse(TickerPrice tickerPrice) {
        this(tickerPrice.getId().getTicker(),
                tickerPrice.getClose(),
                tickerPrice.getCurrency(),
                tickerPrice.getId().getDay(),
                tickerPrice.getSource());
    }
}
