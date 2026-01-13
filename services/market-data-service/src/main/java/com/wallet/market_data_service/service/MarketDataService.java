package com.wallet.market_data_service.service;

import com.wallet.market_data_service.dto.TickerPriceResponse;
import com.wallet.market_data_service.exception.TickerNotFoundException;
import com.wallet.market_data_service.model.TickerPrice;
import com.wallet.market_data_service.repository.TickerPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final TickerPriceRepository tickerPriceRepository;

    public void fetchTickerData(String ticker){

        // call external api for current ticker prices
        // transform into db shape
        // save to database via repository
    }

    public TickerPriceResponse getTickerData(String ticker){
        TickerPrice.Id id = new TickerPrice.Id(ticker, Date.valueOf(LocalDate.now()));
        TickerPrice tickerPrice = tickerPriceRepository.findById(id).orElseThrow(() -> {
            return new TickerNotFoundException("Data for ticker " + ticker + " not found");
        });

        return new TickerPriceResponse(tickerPrice);
    }
}
