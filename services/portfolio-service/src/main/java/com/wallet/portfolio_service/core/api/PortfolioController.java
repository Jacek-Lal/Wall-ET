package com.wallet.portfolio_service.core.api;

import com.wallet.portfolio_service.core.api.dto.PortfolioRequest;
import com.wallet.portfolio_service.core.api.dto.PortfolioResponse;
import com.wallet.portfolio_service.core.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(@RequestBody @Valid PortfolioRequest request){
        PortfolioResponse response = portfolioService.createPortfolio(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{portfolioId}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponse> getPortfolio(@PathVariable UUID portfolioId){
        return ResponseEntity.ok(portfolioService.getPortfolio(portfolioId));
    }

    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> getPortfolios(){
        return ResponseEntity.ok(portfolioService.getPortfolios());
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable UUID portfolioId){
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponse> updatePortfolio(@PathVariable UUID portfolioId,
                                                            @RequestBody @Valid PortfolioRequest request){
        return ResponseEntity.ok(portfolioService.updatePortfolio(portfolioId, request));
    }
}
