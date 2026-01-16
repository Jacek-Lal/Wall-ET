package com.wallet.market_data_service.core.service;

import com.wallet.market_data_service.core.api.dto.TickerPriceResponse;
import com.wallet.market_data_service.core.persistence.entity.TickerPrice;
import com.wallet.market_data_service.core.persistence.repository.TickerPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final TickerPriceRepository tickerPriceRepository;

    public List<TickerPriceResponse> getTickerData(String ticker, LocalDate from, LocalDate to){
        if(from == null)
            from = LocalDate.now();

        if(to == null)
            to = LocalDate.now();

        List<TickerPrice> tickerPrice = tickerPriceRepository.findAllByIdTickerAndIdDayBetween(ticker, from, to);

        return tickerPrice.stream().map(TickerPriceResponse::new).toList();
    }
}
