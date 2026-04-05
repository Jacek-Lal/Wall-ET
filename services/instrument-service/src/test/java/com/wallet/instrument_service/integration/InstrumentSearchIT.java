package com.wallet.instrument_service.integration;

import com.wallet.instrument_service.config.TestcontainersConfig;
import com.wallet.instrument_service.core.api.dto.InstrumentResponse;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import com.wallet.instrument_service.core.persistence.repo.InstrumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
@AutoConfigureRestTestClient
class InstrumentSearchIT {

    @Autowired
    private RestTestClient client;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("app.ticker-api.key", () -> "test-key");
        r.add("app.ticker-api.base-url", () -> "http://localhost:1234");
    }

    @BeforeEach
    void setUp() {
        instrumentRepository.deleteAll();
        instrumentRepository.saveAll(List.of(
                new Instrument("AAPL", "Apple Inc.", Market.STOCKS, "XNAS", "USD", null, InstrumentType.CS, null),
                new Instrument("MSFT", "Microsoft Corporation", Market.STOCKS, "XNAS", "USD", null, InstrumentType.CS, null),
                new Instrument("AAEQ", "Alpha Architect US Equity ETF", Market.STOCKS, "XNAS", "USD", null, InstrumentType.ETF, null),
                new Instrument("X:BTCUSD", "Bitcoin - United States dollar", Market.CRYPTO, null, "USD", "BTC", null, null)
        ));
    }

    @Test
    @DisplayName("Should return matching instruments")
    void search_returnsMatchingInstruments() {
        List<InstrumentResponse> results = client.get()
                .uri(uri -> uri.path("/api/instruments/search")
                        .queryParam("query", "apple")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<InstrumentResponse>>(){})
                .returnResult()
                .getResponseBody();

        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(r -> r.ticker().equals("AAPL"));
    }

    @Test
    @DisplayName("Should return 400 when query is blank")
    void search_returns400_whenQueryBlank() {
        client.get()
                .uri(uri -> uri.path("/api/instruments/search")
                        .queryParam("query", "")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should return empty list when no match")
    void search_returnsEmpty_whenNoMatch() {
        List<InstrumentResponse> results = client.get()
                .uri(uri -> uri.path("/api/instruments/search")
                        .queryParam("query", "zzzzzzzzz")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<InstrumentResponse>>(){})
                .returnResult()
                .getResponseBody();

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Should find by partial word in name")
    void search_findsByPartialWord() {
        List<InstrumentResponse> results = client.get()
                .uri(uri -> uri.path("/api/instruments/search")
                        .queryParam("query", "alph")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<InstrumentResponse>>(){})
                .returnResult()
                .getResponseBody();

        assertThat(results).anyMatch(r -> r.ticker().equals("AAEQ"));
    }
}