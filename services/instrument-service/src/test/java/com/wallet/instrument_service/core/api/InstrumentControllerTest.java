package com.wallet.instrument_service.core.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.instrument_service.core.api.dto.InstrumentRequest;
import com.wallet.instrument_service.core.api.dto.InstrumentResponse;
import com.wallet.instrument_service.core.api.enums.OrderDir;
import com.wallet.instrument_service.core.api.enums.SortBy;
import com.wallet.instrument_service.core.integration.config.TickerClientConfig;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import com.wallet.instrument_service.core.service.InstrumentImportService;
import com.wallet.instrument_service.core.service.InstrumentService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = InstrumentController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = TickerClientConfig.class))
public class InstrumentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private InstrumentService instrumentService;

    @MockitoBean
    private InstrumentImportService importService;

    @Test
    @DisplayName("Should return 200 and list of all instruments")
    void shouldReturnAllInstruments() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<InstrumentResponse> instruments = List.of(
                new InstrumentResponse(1L,"AAPL", "Apple Inc.", Market.STOCKS,
                        "XNYS", "USD",null,
                        InstrumentType.CS, "1003004000", Instant.now()),
                new InstrumentResponse(2L,"A", "Agilent Technologies Inc.", Market.STOCKS,
                        "XNYS","USD",null,
                        InstrumentType.CS, "0002010401", Instant.now())
        );
        Page<InstrumentResponse> page = new PageImpl<>(instruments);

        when(instrumentService.getInstruments(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/instruments"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.content", hasSize(2)),
                        jsonPath("$.content[0].ticker").value("AAPL"),
                        jsonPath("$.content[1].ticker").value("A")
                );
    }

    @Test
    @DisplayName("Should return 200 and instrument details")
    void shouldReturnInstrumentResponse() throws Exception {
        InstrumentResponse response = new InstrumentResponse(1L,"AAPL", "Apple Inc.", Market.STOCKS,
                "XNYS", "USD",null,
                InstrumentType.CS, "1003004000", Instant.now());

        when(instrumentService.getInstrument(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/instruments/{ticker}", "AAPL"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticker").value("AAPL"));
    }

    @Test
    @DisplayName("Should return 200 and call delete on service")
    void shouldDeleteAllInstruments() throws Exception {
        mockMvc.perform(delete("/api/instruments"))
                .andExpect(status().isOk());

        verify(instrumentService, times(1)).deleteInstruments();
    }

    @Nested
    @DisplayName("Creating instrument tests")
    class CreateInstrumentTests {

        @Test
        @DisplayName("Should return 201 Created when valid instrument data is provided")
        void shouldReturnCreatedWhenValidInstrumentData() throws Exception {
            String json = """
                        {
                            "ticker": "AAPL",
                            "name": "Apple Inc.",
                            "market": "STOCKS",
                            "primary_exchange": "XNYS",
                            "currency_symbol": "USD",
                            "type": "CS",
                            "cik": "1003004000"
                        }
                    """;

            InstrumentResponse response = new InstrumentResponse(1L, "AAPL", "Apple Inc.",
                    Market.STOCKS, "XNYS", "USD", "US",
                    InstrumentType.CS, "1003004000", Instant.parse("2026-01-25T18:35:24.00Z"));

            when(instrumentService.createInstrument(any())).thenReturn(response);

            mockMvc.perform(post("/api/instruments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isCreated(),
                            header().exists("Location"),
                            header().string("Location", Matchers.containsString("/api/instruments/1")),
                            jsonPath("$").isNotEmpty(),
                            jsonPath("$.id").value(1),
                            jsonPath("$.ticker").value("AAPL")
                    );
        }

        @ParameterizedTest(name = "invalid field: {1}")
        @MethodSource("invalidRequests")
        @DisplayName("Should return 400 Bad Request when invalid instrument data provided")
        void shouldReturnBadRequestWhenInvalidInstrumentData(InstrumentRequest req, String field) throws Exception {
             mockMvc.perform(post("/api/instruments")
                             .contentType(MediaType.APPLICATION_JSON)
                             .content(objectMapper.writeValueAsString(req)))
                     .andExpect(status().isBadRequest());

            verifyNoInteractions(instrumentService);
         }

        static Stream<Arguments> invalidRequests() {
            InstrumentRequest base = new InstrumentRequest(
                    "AAPL", "Apple Inc.", Market.STOCKS, "XNYS", "USD",
                    null, InstrumentType.CS, "1003004000");

            return Stream.of(
                    Arguments.of(new InstrumentRequest("", base.name(), base.market(), base.primaryExchange(), base.currencySymbol(), base.baseCurrencySymbol(), base.type(), base.cik()), "ticker"),
                    Arguments.of(new InstrumentRequest(base.ticker(), "", base.market(), base.primaryExchange(), base.currencySymbol(), base.baseCurrencySymbol(), base.type(), base.cik()), "name"),
                    Arguments.of(new InstrumentRequest(base.ticker(), base.name(), null, base.primaryExchange(), base.currencySymbol(), base.baseCurrencySymbol(), base.type(), base.cik()), "market"),
                    Arguments.of(new InstrumentRequest(base.ticker(), base.name(), base.market(), base.primaryExchange(), "US", base.baseCurrencySymbol(), base.type(), base.cik()), "currency_symbol"),
                    Arguments.of(new InstrumentRequest(base.ticker(), base.name(), base.market(), base.primaryExchange(), base.currencySymbol(), base.baseCurrencySymbol(), base.type(), "ABC"), "cik")
            );
        }
    }

    @Nested
    @DisplayName("Importing instruments data")
    class ImportInstrumentsTests{

        @Test
        @DisplayName("Should return 200 Ok and call import service")
        void shouldReturnOkWhenValidParameters() throws Exception {
            SortBy sort = SortBy.TICKER;
            OrderDir order = OrderDir.ASC;
            Market market = Market.STOCKS;
            int limit = 1000;

            mockMvc.perform(post("/api/instruments/import")
                            .queryParam("market", market.name())
                            .queryParam("sort", sort.name())
                            .queryParam("order", order.name())
                            .queryParam("limit", String.valueOf(limit)))
                    .andExpect(status().isOk());

            verify(importService, times(1)).fetchInstruments(market, sort, order, limit);
        }

        @ParameterizedTest(name = "invalid param: {4}")
        @MethodSource("invalidParams")
        @DisplayName("Should return 400 Bad Request when invalid parameters and never call import service")
        void shouldReturnBadRequestWhenInvalidParameters(String market, String sort, String order, int limit, String param) throws Exception {
            mockMvc.perform(post("/api/instruments/import")
                            .queryParam("market", market)
                            .queryParam("sort", sort)
                            .queryParam("order", order)
                            .queryParam("limit", String.valueOf(limit)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(importService);
        }

        static Stream<Arguments> invalidParams() {
            return Stream.of(
                    Arguments.of("INVALID", "TICKER", "ASC", 100, "market invalid"),
                    Arguments.of("STOCKS", "INVALID", "ASC", 100, "sort invalid"),
                    Arguments.of("STOCKS", "TICKER", "INVALID", 100, "order invalid"),
                    Arguments.of("STOCKS", "TICKER", "ASC", 0, "limit too low"),
                    Arguments.of("STOCKS", "TICKER", "ASC", 1001, "limit too high")
            );
        }
    }
}
