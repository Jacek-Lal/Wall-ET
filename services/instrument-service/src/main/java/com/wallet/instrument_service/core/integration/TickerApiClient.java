package com.wallet.instrument_service.core.integration;

import com.wallet.instrument_service.core.integration.dto.TickerApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class TickerApiClient {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${app.ticker-api.key}") String API_KEY;
    @Value("${app.ticker-api.url}") String API_URL;

    public TickerApiResponse fetchPage(String sort, String order, int limit, String nextUrl){
        FetchRequest request = nextUrl == null ?
                FetchRequest.initial(sort, order, limit) :
                FetchRequest.next(nextUrl);

        URI uri = buildUri(request);

        String json = fetchJson(uri);

        return parseResponse(json);
    }

    private URI buildUri(FetchRequest req) {
        if (req.nextUrl() != null) {
            return UriComponentsBuilder.fromUriString(req.nextUrl())
                    .queryParam("apiKey", API_KEY)
                    .build(true)
                    .toUri();
        }

        return UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("active", "true")
                .queryParam("order", req.order())
                .queryParam("limit", req.limit())
                .queryParam("sort", req.sort())
                .queryParam("apiKey", API_KEY)
                .build(true)
                .toUri();
    }

    private String fetchJson(URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(10))
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("HTTP " + response.statusCode() + " from " + uri);
            }
            return response.body();
        } catch (Exception e){
            throw new IllegalStateException("Failed to fetch instruments from " + uri, e);
        }
    }

    private TickerApiResponse parseResponse(String json) {
        try {
            return objectMapper.readValue(json, TickerApiResponse.class);
        } catch (Exception e){
            throw new IllegalStateException("Failed to parse ticker API response", e);
        }
    }

    private record FetchRequest(String nextUrl, String sort, String order, Integer limit){
        static FetchRequest initial(String sort, String order, int limit){
            return new FetchRequest(null, sort, order, limit);
        }
        static FetchRequest next(String nextUrl){
            return new FetchRequest(nextUrl, null, null, null);
        }
    }
}

