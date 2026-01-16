package com.wallet.market_data_service.core.api;

import com.wallet.market_data_service.core.api.dto.TickerPriceResponse;
import com.wallet.market_data_service.core.service.DataImportService;
import com.wallet.market_data_service.core.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/marketdata")
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketDataService marketDataService;
    private final DataImportService dataImportService;

    @GetMapping("/{ticker}")
    public ResponseEntity<List<TickerPriceResponse>> getTickerData(@PathVariable String ticker,
                                                                   @RequestParam(required = false) LocalDate from,
                                                                   @RequestParam(required = false) LocalDate to){
        return ResponseEntity.ok(marketDataService.getTickerData(ticker, from, to));
    }

    @PostMapping("/{ticker}/import")
    public ResponseEntity<String> importTickerData(@PathVariable String ticker){
        dataImportService.fetchTickerData(ticker);
        return ResponseEntity.ok().build();
    }
}
