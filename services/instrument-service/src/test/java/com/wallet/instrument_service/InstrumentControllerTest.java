package com.wallet.instrument_service;

import com.wallet.instrument_service.core.api.InstrumentController;
import com.wallet.instrument_service.core.api.dto.InstrumentRequest;
import com.wallet.instrument_service.core.api.dto.InstrumentResponse;
import com.wallet.instrument_service.core.api.enums.OrderDir;
import com.wallet.instrument_service.core.api.enums.SortBy;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstrumentController.class)
public class InstrumentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean InstrumentService instrumentService;
    @MockitoBean InstrumentImportService importService;

    @Test
    @DisplayName("Should return 200 and list of all instruments")
    void shouldReturnAllInstruments() throws Exception {
        List<InstrumentRequest> instruments = List.of(
                new InstrumentRequest("AAPL", "Apple Inc.", "XNYS",
                        "US", "USD","stocks","CS", "100300400"),
                new InstrumentRequest("A", "Agilent Technologies Inc.","XNYS",
                        "US","USD","stocks","CS", "2010401")
        );

        when(instrumentService.getAllInstruments()).thenReturn(instruments);

        mockMvc.perform(get("/api/instruments"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].ticker").value("AAPL"),
                        jsonPath("$[1].ticker").value("A")
                );
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
                            "exchange": "XNYS",
                            "country": "US",
                            "currency": "USD",
                            "market": "stocks",
                            "asset_type": "CS",
                            "cik": "100300400"
                        }
                    """;

            InstrumentResponse response = new InstrumentResponse(1L, "AAPL", "Apple Inc.",
                    "XNYS", "US", "USD","stocks","CS", "100300400",
                    Instant.parse("2026-01-25T18:35:24.00Z"));

            when(instrumentService.createInstrument(any())).thenReturn(response);

            mockMvc.perform(post("/api/instruments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isCreated(),
                            header().exists("Location"),
                            header().string("Location", Matchers.containsString("/api/instruments/1")),
                            jsonPath("$").isNotEmpty(),
                            jsonPath("$.id").value(1L),
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
                    "AAPL", "Apple Inc.", "XNYS", "US", "USD", "stocks", "CS", "100300400"
            );

            return Stream.of(
                    Arguments.of(new InstrumentRequest("", base.name(), base.exchange(), base.country(), base.currency(), base.market(), base.asset_type(), base.cik()), "ticker"),
                    Arguments.of(new InstrumentRequest(base.ticker(), "", base.exchange(), base.country(), base.currency(), base.market(), base.asset_type(), base.cik()), "name"),
                    Arguments.of(new InstrumentRequest(base.ticker(), base.name(), "", base.country(), base.currency(), base.market(), base.asset_type(), base.cik()), "exchange"),
                    Arguments.of(new InstrumentRequest(base.ticker(), base.name(), base.exchange(), "U", base.currency(), base.market(), base.asset_type(), base.cik()), "country"),
                    Arguments.of(new InstrumentRequest(base.ticker(), base.name(), base.exchange(), base.country(), "US", base.market(), base.asset_type(), base.cik()), "currency"),
                    Arguments.of(new InstrumentRequest(base.ticker(), base.name(), base.exchange(), base.country(), base.currency(), "", base.asset_type(), base.cik()), "market"),
                    Arguments.of(new InstrumentRequest(base.ticker(), base.name(), base.exchange(), base.country(), base.currency(), base.market(), base.asset_type(), "ABC"), "cik")
            );
        }
    }


    @Nested
    @DisplayName("Importing instruments data")
    class ImportInstrumentsTests{

        @Test
        @DisplayName("Should return 200 Ok and call import service")
        void shouldReturnOkWhenValidParameters() throws Exception {
            SortBy sort = SortBy.ticker;
            OrderDir order = OrderDir.asc;
            int limit = 1000;

            mockMvc.perform(post("/api/instruments/import")
                            .queryParam("sort", sort.name())
                            .queryParam("order", order.name())
                            .queryParam("limit", String.valueOf(limit)))
                    .andExpect(status().isOk());

            verify(importService, times(1)).fetchInstruments(sort, order, limit);
        }

        @ParameterizedTest(name = "invalid param: {3}")
        @MethodSource("invalidParams")
        @DisplayName("Should return 400 Bad Request when invalid parameters and never call import service")
        void shouldReturnBadRequestWhenInvalidParameters(String sort, String order, int limit, String param) throws Exception {
            mockMvc.perform(post("/api/instruments/import")
                            .queryParam("sort", sort)
                            .queryParam("order", order)
                            .queryParam("limit", String.valueOf(limit)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(importService);
        }

        static Stream<Arguments> invalidParams(){
            return Stream.of(
                    Arguments.of("", "asc", 1000, "ticker blank"),
                    Arguments.of("ticke", "asc", 1000, "ticker invalid"),
                    Arguments.of("ticker", "", 1000, "order blank"),
                    Arguments.of("ticker", "qwe", 1000, "order invalid"),
                    Arguments.of("ticker", "asc", 0, "limit too low"),
                    Arguments.of("ticker", "asc", 1001, "limit too high")
            );
        }
    }
}
