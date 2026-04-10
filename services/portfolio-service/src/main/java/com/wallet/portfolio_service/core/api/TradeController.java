package com.wallet.portfolio_service.core.api;

import com.wallet.portfolio_service.core.api.dto.TradeRequest;
import com.wallet.portfolio_service.core.api.dto.TradeResponse;
import com.wallet.portfolio_service.core.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping
    public ResponseEntity<TradeResponse> createTrade(@PathVariable UUID portfolioId,
                                                     @RequestBody @Valid TradeRequest request){

        TradeResponse response = tradeService.createTrade(portfolioId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{portfolioId}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{tradeId}")
    public ResponseEntity<TradeResponse> getTrade(@PathVariable UUID portfolioId,
                                                  @PathVariable UUID tradeId){
        return ResponseEntity.ok().body(tradeService.getTrade(portfolioId, tradeId));
    }

    @GetMapping
    public ResponseEntity<List<TradeResponse>> getTrades(@PathVariable UUID portfolioId){
        return ResponseEntity.ok().body(tradeService.getTrades(portfolioId));
    }

    @DeleteMapping("/{tradeId}")
    public ResponseEntity<Void> deleteTrade(@PathVariable UUID portfolioId,
                                            @PathVariable UUID tradeId){
        tradeService.deleteTrade(portfolioId, tradeId);
        return ResponseEntity.noContent().build();
    }

}
