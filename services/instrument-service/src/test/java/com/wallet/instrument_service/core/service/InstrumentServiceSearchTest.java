package com.wallet.instrument_service.core.service;

import com.wallet.instrument_service.core.api.dto.InstrumentResponse;
import com.wallet.instrument_service.core.mapper.InstrumentMapper;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import com.wallet.instrument_service.core.persistence.repo.InstrumentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstrumentServiceSearchTest {

    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private InstrumentMapper instrumentMapper;

    @InjectMocks
    private InstrumentService instrumentService;

    @Test
    @DisplayName("Search should return empty list when query is null")
    void search_returnsEmptyList_whenQueryNull() {
        assertThat(instrumentService.search(null)).isEmpty();
        verifyNoInteractions(instrumentRepository);
    }

    @Test
    @DisplayName("Search should return empty list when query is blank")
    void search_returnsEmptyList_whenQueryBlank() {
        assertThat(instrumentService.search("   ")).isEmpty();
        verifyNoInteractions(instrumentRepository);
    }

    @Test
    @DisplayName("Search should trim query before passing to repository")
    void search_trimsQuery_beforePassingToRepository() {
        when(instrumentRepository.search("alph")).thenReturn(List.of());

        instrumentService.search("  alph  ");

        verify(instrumentRepository).search("alph");
    }

    @Test
    @DisplayName("Search should map repository results to responses")
    void search_mapsResultsToResponses() {
        Instrument instrument = createInstrument("AAPL", "Apple Inc.");
        InstrumentResponse response = createResponse("AAPL", "Apple Inc.");

        when(instrumentRepository.search("aapl")).thenReturn(List.of(instrument));
        when(instrumentMapper.toResponse(instrument)).thenReturn(response);

        List<InstrumentResponse> result = instrumentService.search("aapl");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().ticker()).isEqualTo("AAPL");
        verify(instrumentMapper).toResponse(instrument);
    }

    @Test
    @DisplayName("Search should return empty list when no results found")
    void search_returnsEmptyList_whenNoResults() {
        when(instrumentRepository.search("xyz123")).thenReturn(List.of());

        assertThat(instrumentService.search("xyz123")).isEmpty();
    }

    private Instrument createInstrument(String ticker, String name) {
        return new Instrument(ticker, name, Market.STOCKS, "XNAS", "USD",
                null, InstrumentType.CS, null);
    }

    private InstrumentResponse createResponse(String ticker, String name) {
        return new InstrumentResponse(1L, ticker, name, Market.STOCKS, "XNAS", "USD",
                null, InstrumentType.CS, null, Instant.now());
    }
}
