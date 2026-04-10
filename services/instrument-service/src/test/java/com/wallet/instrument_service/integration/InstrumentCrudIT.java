package com.wallet.instrument_service.integration;

import com.wallet.instrument_service.config.TestcontainersConfig;
import com.wallet.instrument_service.core.api.dto.InstrumentRequest;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
@AutoConfigureRestTestClient
class InstrumentCrudIT {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private RestTestClient client;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("app.ticker-api.key", () -> "test-key");
        r.add("app.ticker-api.base-url", () -> "http://localhost:1234/tickers");
    }

    @BeforeEach
    void cleanup() {
        instrumentRepository.deleteAll();
        Instrument i = new Instrument("AAPL", "Apple Inc.",
                Market.STOCKS, "XNYS", "USD", null, InstrumentType.CS,
                "1003004000");

        instrumentRepository.save(i);
    }

    @Test
    @DisplayName("Should create instrument with valid request, then return it with location")
    void shouldCreateThenReturnInstrument_whenRequestValid() {
        InstrumentRequest req = new InstrumentRequest(
                "MSFT", "Microsoft", Market.STOCKS,
                "XNYS", "USD", null, InstrumentType.CS, "1234567890"
        );

        InstrumentResponse response = client.post()
                .uri("/api/instruments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().value("Location", location ->
                        assertThat(location).contains("/api/instruments/"))
                .expectBody(InstrumentResponse.class)
                .returnResult()
                .getResponseBody();


        InstrumentResponse expected = new InstrumentResponse(null, "MSFT", "Microsoft", Market.STOCKS,
                "XNYS", "USD", null, InstrumentType.CS,
                "1234567890", null
        );
        assertThat(response).isNotNull();
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(expected);
        assertThat(response.createdAt()).isNotNull();

        assertThat(instrumentRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return 400 Bad Request with invalid request and don't persist instrument")
    void shouldReturnBadRequest_whenRequestInvalid() {
        InstrumentRequest invalid = new InstrumentRequest(
                "", "Apple Inc.", Market.STOCKS, "XNYS",
                "USD", "US", InstrumentType.CS, "100300400"
        );

        client.post()
                .uri("/api/instruments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalid)
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(instrumentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return instrument details when instrument with given ticker exists")
    void shouldReturnInstrument_whenTickerExists() {
        String ticker = "AAPL";
        InstrumentResponse response = client.get()
                .uri("/api/instruments/{ticker}", ticker)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InstrumentResponse.class)
                .returnResult()
                .getResponseBody();

        InstrumentResponse expected = new InstrumentResponse(null, "AAPL", "Apple Inc.",
                Market.STOCKS, "XNYS", "USD", null, InstrumentType.CS,
                "1003004000", null
        );
        assertThat(response).isNotNull();
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(expected);
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("Should return 404 Not Found when instrument with given ticker does not exist")
    void shouldReturnNotFound_whenNonExistentTicker(){
        String ticker = "X";
        client.get()
                .uri("/api/instruments/{ticker}", ticker)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should delete all instruments")
    void shouldDeleteAllInstruments() {

        client.delete().uri("/api/instruments").exchange().expectStatus().isOk();

        assertThat(instrumentRepository.findAll()).isEmpty();
    }
}
