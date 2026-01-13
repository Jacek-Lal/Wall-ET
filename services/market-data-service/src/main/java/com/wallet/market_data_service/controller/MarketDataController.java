package com.wallet.market_data_service.controller;

import com.wallet.market_data_service.dto.TickerPriceResponse;
import com.wallet.market_data_service.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marketdata")
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketDataService marketDataService;

    @GetMapping("/{ticker}")
    public ResponseEntity<TickerPriceResponse> getTickerData(@PathVariable String ticker){
        return ResponseEntity.ok(marketDataService.getTickerData(ticker));
    }
}
