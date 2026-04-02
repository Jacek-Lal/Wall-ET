package com.wallet.instrument_service.core.integration;

import com.wallet.instrument_service.core.integration.dto.FullResponse;
import com.wallet.instrument_service.core.integration.dto.TickerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TickerApiClientTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    TickerApiClient client;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "apiKey", "test-key");
    }

    @Test
    @DisplayName("fetch should call REST with composed uri and return mapped response")
    void fetch_withParams_callsUriAndReturnsResponse() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(java.util.function.Function.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();

        FullResponse resp = new FullResponse(List.of(createTicker("AAA")), "OK", "r", 1, null);
        doReturn(resp).when(responseSpec).body(eq(FullResponse.class));

        FullResponse result = client.fetch("stocks","ticker","asc", 10);

        assertThat(result).isSameAs(resp);
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(any(java.util.function.Function.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).body(FullResponse.class);
    }

    @Test
    @DisplayName("fetchNext should call REST with nextUrl and return mapped response")
    void fetchNext_withNextUrl_callsUriAndReturnsResponse() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(URI.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();

        String next = "http://example.com/tickers?page=2";
        FullResponse resp = new FullResponse(List.of(createTicker("AAA")), "OK", "r", 1, null);
        doReturn(resp).when(responseSpec).body(eq(FullResponse.class));

        FullResponse result = client.fetchNext(next);

        assertThat(result).isSameAs(resp);
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(any(URI.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).body(FullResponse.class);
    }

    @Test
    @DisplayName("fetch should wrap RestClientException into IllegalStateException")
    void fetch_restClientThrowsWrappedInIllegalStateException() {
        doThrow(new RestClientException("failed")).when(restClient).get();
        assertThrows(IllegalStateException.class, () -> client.fetch("stocks","ticker","asc", 10));
    }

    private TickerResponse createTicker(String ticker) {
        return new TickerResponse(
                ticker, ticker + " Name", "Market", "EX", "Type",
                "USD", "USD_NAME", "US", "100", true);
    }
}
