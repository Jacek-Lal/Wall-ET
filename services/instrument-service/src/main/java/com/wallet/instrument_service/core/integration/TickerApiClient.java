package com.wallet.instrument_service.core.integration;

import com.wallet.instrument_service.core.integration.dto.TickerApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class TickerApiClient {

    private final RestClient restClient;

    @Value("${app.ticker-api.key}") String apiKey;

    public TickerApiResponse fetchPage(String sort, String order, int limit, String nextUrl) {
        try {
            if (nextUrl != null && !nextUrl.isBlank()) {
                URI uri = UriComponentsBuilder.fromUriString(nextUrl)
                        .replaceQueryParam("apiKey", apiKey)
                        .build(true)
                        .toUri();

                return restClient.get()
                        .uri(uri)
                        .retrieve()
                        .body(TickerApiResponse.class);
            }

            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("active", "true")
                            .queryParam("order", order)
                            .queryParam("limit", limit)
                            .queryParam("sort", sort)
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .body(TickerApiResponse.class);

        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to fetch instruments (sort=%s, order=%s, limit=%d, nextUrl=%s)"
                    .formatted(sort, order, limit, nextUrl), e);
        }
    }
}
