package com.wallet.instrument_service.it;

import com.wallet.instrument_service.core.api.dto.InstrumentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestRestTemplate
class InstrumentCrudIT {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.flyway.enabled", () -> true);
        r.add("app.ticker-api.key", () -> "test-key");
        r.add("app.ticker-api.base-url", () -> "http://localhost:1234/tickers");
    }

    @Autowired
    TestRestTemplate http;

    @BeforeEach
    void cleanup() {
        http.exchange("/api/instruments", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
    }

    @Test
    @DisplayName("Should create instrument with valid request, then return all instruments in list")
    void shouldCreateThenReturnAllInstruments_whenRequestValid() {
        InstrumentRequest req = new InstrumentRequest(
                "AAPL", "Apple Inc.", "XNYS",
                "US", "USD", "stocks", "CS", "100300400"
        );

        ResponseEntity<String> created = http.postForEntity("/api/instruments", req, String.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getHeaders().getLocation()).isNotNull();
        assertThat(created.getBody()).isNotBlank();

        ResponseEntity<InstrumentRequest[]> all = http.getForEntity("/api/instruments", InstrumentRequest[].class);

        assertThat(all.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(all.getBody()).isNotNull();
        assertThat(all.getBody()).hasSize(1);
        assertThat(all.getBody()[0].ticker()).isEqualTo("AAPL");
    }

    @Test
    @DisplayName("Should return 400 Bad Request with invalid request and don't persist instrument")
    void shouldReturnBadRequest_whenRequestInvalid() {
        InstrumentRequest invalid = new InstrumentRequest(
                "", "Apple Inc.", "XNYS",
                "US", "USD", "stocks", "CS", "100300400"
        );

        ResponseEntity<String> res = http.postForEntity("/api/instruments", invalid, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ResponseEntity<InstrumentRequest[]> all = http.getForEntity("/api/instruments", InstrumentRequest[].class);
        assertThat(all.getBody()).isNotNull();
        assertThat(all.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Should delete all instruments")
    void shouldDeleteAllInstruments() {
        InstrumentRequest req1 = new InstrumentRequest("AAPL", "Apple Inc.", "XNYS", "US", "USD", "stocks", "CS", "100300400");
        InstrumentRequest req2 = new InstrumentRequest("MSFT", "Microsoft", "XNYS", "US", "USD", "stocks", "CS", "123456789");

        assertThat(http.postForEntity("/api/instruments", req1, String.class).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(http.postForEntity("/api/instruments", req2, String.class).getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> del = http.exchange("/api/instruments", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<InstrumentRequest[]> all = http.getForEntity("/api/instruments", InstrumentRequest[].class);
        assertThat(all.getBody()).isNotNull();
        assertThat(all.getBody()).isEmpty();
    }
}
