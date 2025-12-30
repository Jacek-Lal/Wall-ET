package com.wallet.instrument_service.service;

import com.wallet.instrument_service.dto.TickerDTO;
import com.wallet.instrument_service.model.Instrument;
import com.wallet.instrument_service.model.SyncState;
import com.wallet.instrument_service.repository.SyncStateRepository;
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
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstrumentImportService {

    private final SyncStateRepository stateRepository;
    private final InstrumentImportTx importTx;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${app.ticker-api.key}") String API_KEY;
    @Value("${app.ticker-api.url}") String API_URL;

    public void fetchInstruments(String sort, String order, int limit){
        log.info("Instruments import started with: sort = {}, order = {}", sort, order);
        SyncState lastState = stateRepository.findLastStateBySortAndDir(sort, order).orElse(null);

        FetchRequest request = lastState == null ?
                FetchRequest.initial(sort, order, limit) :
                FetchRequest.next(lastState.getNextUrl(), lastState.getSortBy(), lastState.getSortDir());

        URI uri = buildUri(request);

        String json = fetchJson(uri);
        TickerApiResponse response = parseResponse(json);

        importTx.deleteSyncState(lastState);
        importTx.saveSyncState(response.count(), request.order(), request.sort(), response.next_url());

        List<Instrument> instruments = response
                .results()
                .stream()
                .map(Instrument::new)
                .toList();

        int[][] results = importTx.upsertIgnoreDuplicates(instruments);
        int inserted = Arrays.stream(results).flatMapToInt(Arrays::stream).sum();
        log.info("Import finished: fetched={}, inserted={}, skipped={}",
                instruments.size(), inserted, instruments.size() - inserted);
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
        static FetchRequest next(String nextUrl, String sort, String order){
            return new FetchRequest(nextUrl, sort, order, null);
        }
    }

    private record TickerApiResponse(List<TickerDTO> results, String status, String request_id,
                                     int count, String next_url) {}
}

