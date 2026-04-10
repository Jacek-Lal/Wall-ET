package com.wallet.portfolio_service.core.integration;

import com.wallet.portfolio_service.core.integration.dto.InstrumentResponse;
import com.wallet.portfolio_service.exception.ResourceNotFoundException;
import com.wallet.portfolio_service.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class InstrumentServiceClient {

    private final RestClient restClient;

    public InstrumentResponse getByTicker(String ticker) {
        return restClient.get()
                .uri("/api/instruments/{ticker}", ticker)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new ResourceNotFoundException(
                            "Instrument with ticker %s does not exist".formatted(ticker));
                })
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new ServiceUnavailableException("Instrument service is currently unavailable");
                }))
                .body(InstrumentResponse.class);
    }
}
