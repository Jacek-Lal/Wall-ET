package com.wallet.instrument_service.service;

import com.wallet.instrument_service.dto.InstrumentDTO;
import com.wallet.instrument_service.dto.TickerApiResponse;
import com.wallet.instrument_service.model.Instrument;
import com.wallet.instrument_service.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    @Value("${app.ticker-api.key}") String API_KEY;
    @Value("${app.ticker-api.url}") String API_URL;

    public void createInstrument(InstrumentDTO request) {
        Instrument instrument = new Instrument(request);
        instrumentRepository.save(instrument);
    }

    public List<InstrumentDTO> getAllInstruments() {
        List<Instrument> instruments = instrumentRepository.findAll();
        return instruments.stream()
                .map(instrument -> new InstrumentDTO(
                        instrument.getTicker(),
                        instrument.getName(),
                        instrument.getExchange(),
                        instrument.getCountry(),
                        instrument.getCurrency(),
                        instrument.getMarket(),
                        instrument.getAssetType(),
                        instrument.getCik()
                ))
                .toList();
    }

    public void fetchInstruments(){
        String uri = API_URL +
                "?active=true" +
                "&order=asc" +
                "&limit=1000" +
                "&sort=primary_exchange" +
                "&apiKey=" + API_KEY;

        String jsonResponse = "";

        try (HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            jsonResponse = httpResponse.body();
            log.info("Instruments fetched successfully");
        } catch (Exception e){
            log.error("Error fetching instruments: {}", e.getMessage());
        }

        ObjectMapper mapper = new ObjectMapper();
        TickerApiResponse response = mapper.readValue(jsonResponse, TickerApiResponse.class);
        List<Instrument> instruments = response
                .results()
                .stream()
                .map(Instrument::new)
                .toList();

        instrumentRepository.saveAll(instruments);
        log.info("{} Instruments saved to database successfully", instruments.size());
    }

    public void deleteInstruments() {
        instrumentRepository.deleteAll();
        log.info("Instruments wiped");
    }
}
