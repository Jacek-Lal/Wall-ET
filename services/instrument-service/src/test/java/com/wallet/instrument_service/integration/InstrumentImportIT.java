package com.wallet.instrument_service.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.wallet.instrument_service.core.api.enums.OrderDir;
import com.wallet.instrument_service.core.api.enums.SortBy;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.entity.SyncState;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import com.wallet.instrument_service.core.persistence.repo.InstrumentRepository;
import com.wallet.instrument_service.core.persistence.repo.SyncStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestcontainersConfig.class)
@ActiveProfiles("test")
class InstrumentImportIT {

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("app.ticker-api.base-url", () -> wm.baseUrl() + "/v3/reference/tickers");
        registry.add("app.ticker-api.key", () -> "test-api-key");
    }

    @Autowired
    private RestTestClient client;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private SyncStateRepository syncStateRepository;

    @BeforeEach
    void setUp() {
        instrumentRepository.deleteAll();
        syncStateRepository.deleteAll();
        wm.resetAll();
    }

    @Test
    void shouldImportStocksSuccessfully() {
        wm.stubFor(get(urlPathEqualTo("/v3/reference/tickers"))
                .withQueryParam("market", equalTo("stocks"))
                .withQueryParam("active", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(stocksResponse())));

        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/instruments/import")
                        .queryParam("market", "STOCKS")
                        .queryParam("sort", "TICKER")
                        .queryParam("order", "ASC")
                        .queryParam("limit", "100")
                        .build())
                .exchange()
                .expectStatus().isOk();

        List<Instrument> instruments = instrumentRepository.findAll();
        assertThat(instruments).hasSize(2);

        Instrument aapl = instruments.stream()
                .filter(i -> i.getTicker().equals("AAPL"))
                .findFirst().orElseThrow();

        assertThat(aapl.getName()).isEqualTo("Apple Inc.");
        assertThat(aapl.getMarket()).isEqualTo(Market.STOCKS);
        assertThat(aapl.getCurrencySymbol()).isEqualTo("USD");
        assertThat(aapl.getPrimaryExchange()).isEqualTo("XNAS");
        assertThat(aapl.getType()).isEqualTo(InstrumentType.CS);
    }

    @Test
    void shouldSaveSyncStateWhenNextUrlPresent() {
        wm.stubFor(get(urlPathEqualTo("/v3/reference/tickers"))
                .withQueryParam("market", equalTo("stocks"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(stocksResponseWithNextUrl(
                                wm.baseUrl() + "/v3/reference/tickers?cursor=abc123"))));

        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/instruments/import")
                        .queryParam("market", "STOCKS")
                        .queryParam("sort", "TICKER")
                        .queryParam("order", "ASC")
                        .queryParam("limit", "100")
                        .build())
                .exchange()
                .expectStatus().isOk();

        assertThat(syncStateRepository.findAll()).hasSize(1);
        assertThat(syncStateRepository.findAll().get(0).getNextUrl()).contains("cursor=abc123");
    }

    @Test
    void shouldNotSaveSyncStateWhenNextUrlAbsent() {
        wm.stubFor(get(urlPathEqualTo("/v3/reference/tickers"))
                .withQueryParam("market", equalTo("stocks"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(stocksResponse())));

        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/instruments/import")
                        .queryParam("market", "STOCKS")
                        .queryParam("sort", "TICKER")
                        .queryParam("order", "ASC")
                        .queryParam("limit", "100")
                        .build())
                .exchange()
                .expectStatus().isOk();

        assertThat(syncStateRepository.findAll()).isEmpty();
    }

    @Test
    void shouldImportCryptoAndResolveCurrencySymbol() {
        wm.stubFor(get(urlPathEqualTo("/v3/reference/tickers"))
                .withQueryParam("market", equalTo("crypto"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(cryptoResponse())));

        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/instruments/import")
                        .queryParam("market", "CRYPTO")
                        .queryParam("sort", "TICKER")
                        .queryParam("order", "ASC")
                        .queryParam("limit", "100")
                        .build())
                .exchange()
                .expectStatus().isOk();

        Instrument btc = instrumentRepository.findAll().getFirst();
        assertThat(btc.getMarket()).isEqualTo(Market.CRYPTO);
        assertThat(btc.getCurrencySymbol()).isEqualTo("USD");
        assertThat(btc.getBaseCurrencySymbol()).isEqualTo("BTC");
        assertThat(btc.getPrimaryExchange()).isNull();
    }

    @Test
    void shouldUseSyncStateNextUrlForSubsequentImport() {
        syncStateRepository.save(new SyncState(
                100, Market.STOCKS, OrderDir.ASC, SortBy.TICKER,
                wm.baseUrl() + "/v3/reference/tickers?cursor=abc123"));

        wm.stubFor(get(urlPathEqualTo("/v3/reference/tickers"))
                .withQueryParam("cursor", equalTo("abc123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(stocksResponse())));

        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/instruments/import")
                        .queryParam("market", "STOCKS")
                        .queryParam("sort", "TICKER")
                        .queryParam("order", "ASC")
                        .queryParam("limit", "100")
                        .build())
                .exchange()
                .expectStatus().isOk();


        wm.verify(getRequestedFor(urlPathEqualTo("/v3/reference/tickers"))
                .withQueryParam("cursor", equalTo("abc123")));
    }

    @Test
    void shouldSkipDuplicatesOnConflict() {
        wm.stubFor(get(urlPathEqualTo("/v3/reference/tickers"))
                .withQueryParam("market", equalTo("stocks"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(stocksResponse())));

        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/instruments/import")
                        .queryParam("market", "STOCKS")
                        .queryParam("sort", "TICKER")
                        .queryParam("order", "ASC")
                        .queryParam("limit", "100")
                        .build())
                .exchange()
                .expectStatus().isOk();

        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/instruments/import")
                        .queryParam("market", "STOCKS")
                        .queryParam("sort", "TICKER")
                        .queryParam("order", "ASC")
                        .queryParam("limit", "100")
                        .build())
                .exchange()
                .expectStatus().isOk();


        assertThat(instrumentRepository.findAll()).hasSize(2);
    }

    @Test
    void shouldReturn500WhenApiReturnsError() {
        wm.stubFor(get(urlPathEqualTo("/v3/reference/tickers"))
                .willReturn(aResponse().withStatus(500)));

        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/instruments/import")
                        .queryParam("market", "STOCKS")
                        .queryParam("sort", "TICKER")
                        .queryParam("order", "ASC")
                        .queryParam("limit", "100")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void shouldReturnOkWhenApiReturnsEmptyResults() {
        wm.stubFor(get(urlPathEqualTo("/v3/reference/tickers"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(emptyResponse())));

        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/instruments/import")
                        .queryParam("market", "STOCKS")
                        .queryParam("sort", "TICKER")
                        .queryParam("order", "ASC")
                        .queryParam("limit", "100")
                        .build())
                .exchange()
                .expectStatus().isOk();

        assertThat(instrumentRepository.findAll()).isEmpty();
    }

    private String stocksResponse() {
        return """
                {
                    "results": [
                        {
                            "ticker": "AAPL",
                            "name": "Apple Inc.",
                            "market": "stocks",
                            "locale": "us",
                            "primary_exchange": "XNAS",
                            "type": "CS",
                            "active": true,
                            "currency_name": "usd",
                            "cik": "0000320193",
                            "last_updated_utc": "2026-01-01T00:00:00Z"
                        },
                        {
                            "ticker": "MSFT",
                            "name": "Microsoft Corporation",
                            "market": "stocks",
                            "locale": "us",
                            "primary_exchange": "XNAS",
                            "type": "CS",
                            "active": true,
                            "currency_name": "usd",
                            "last_updated_utc": "2026-01-01T00:00:00Z"
                        }
                    ],
                    "status": "OK",
                    "request_id": "test-request-id",
                    "count": 2
                }
                """;
    }

    private String stocksResponseWithNextUrl(String nextUrl) {
        return """
                {
                    "results": [
                        {
                            "ticker": "AAPL",
                            "name": "Apple Inc.",
                            "market": "stocks",
                            "locale": "us",
                            "primary_exchange": "XNAS",
                            "type": "CS",
                            "active": true,
                            "currency_name": "usd"
                        }
                    ],
                    "status": "OK",
                    "request_id": "test-request-id",
                    "count": 1,
                    "next_url": "%s"
                }
                """.formatted(nextUrl);
    }

    private String cryptoResponse() {
        return """
                {
                    "results": [
                        {
                            "ticker": "X:BTCUSD",
                            "name": "Bitcoin - United States dollar",
                            "market": "crypto",
                            "locale": "global",
                            "active": true,
                            "currency_symbol": "USD",
                            "base_currency_symbol": "BTC",
                            "base_currency_name": "Bitcoin",
                            "last_updated_utc": "2026-01-01T00:00:00Z"
                        }
                    ],
                    "status": "OK",
                    "request_id": "test-request-id",
                    "count": 1
                }
                """;
    }

    private String emptyResponse() {
        return """
                {
                    "results": [],
                    "status": "OK",
                    "request_id": "test-request-id",
                    "count": 0
                }
                """;
    }
}