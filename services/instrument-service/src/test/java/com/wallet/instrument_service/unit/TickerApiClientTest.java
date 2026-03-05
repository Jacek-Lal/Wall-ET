package com.wallet.instrument_service.unit;

import com.wallet.instrument_service.core.integration.TickerApiClient;
import com.wallet.instrument_service.core.integration.dto.TickerApiResponse;
import com.wallet.instrument_service.core.integration.dto.TickerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
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

    @Mock RestClient restClient;
    @Mock RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    @Mock RestClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock RestClient.ResponseSpec responseSpec;

    @InjectMocks
    TickerApiClient client;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "apiKey", "test-key");
    }

    @Test
    @DisplayName("fetchPage should call REST with nextUrl and return mapped response")
    void fetchPage_withNextUrl_callsUriWithNextUrlAndReturnsResponse() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(URI.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();

        String next = "http://example.com/tickers?page=2";
        TickerApiResponse resp = new TickerApiResponse(List.of(createTicker("AAA")), "OK", "r", 1, null);
        doReturn(resp).when(responseSpec).body(eq(TickerApiResponse.class));

        TickerApiResponse result = client.fetchPage("ticker","asc", 10, next);

        assertThat(result).isSameAs(resp);
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(any(URI.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).body(TickerApiResponse.class);
    }

    @Test
    @DisplayName("fetchPage should wrap RestClientException into IllegalStateException")
    void fetchPage_restClientThrowsWrappedInIllegalStateException() {

        doThrow(new RestClientException("failed")).when(restClient).get();
        assertThrows(IllegalStateException.class, () -> client.fetchPage("ticker","asc", 10, null));
    }

    private TickerDTO createTicker(String ticker) {
        return new TickerDTO(
                ticker,
                ticker + " Name",
                "Market",
                "EX",
                "Type",
                "USD",
                "CIK",
                "US"
        );
    }
}
