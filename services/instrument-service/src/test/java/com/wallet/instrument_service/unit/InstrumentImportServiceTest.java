package com.wallet.instrument_service.unit;

import com.wallet.instrument_service.core.api.enums.OrderDir;
import com.wallet.instrument_service.core.api.enums.SortBy;
import com.wallet.instrument_service.core.integration.TickerApiClient;
import com.wallet.instrument_service.core.integration.dto.TickerApiResponse;
import com.wallet.instrument_service.core.integration.dto.TickerDTO;
import com.wallet.instrument_service.core.persistence.dao.InstrumentSyncWriter;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.entity.SyncState;
import com.wallet.instrument_service.core.persistence.repo.SyncStateRepository;
import com.wallet.instrument_service.core.service.InstrumentImportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstrumentImportServiceTest {

    @Mock TickerApiClient tickerClient;
    @Mock SyncStateRepository syncStateRepository;
    @Mock InstrumentSyncWriter writer;

    @InjectMocks
    InstrumentImportService importService;

    @Test
    @DisplayName("Should fetch instruments and don't persist page")
    void fetchInstruments_noResults_doesNotCallWriter() {
        when(syncStateRepository.findTopBySortByAndSortDirOrderByIdDesc(eq("ticker"), eq("asc")))
                .thenReturn(Optional.empty());

        TickerApiResponse emptyResp = new TickerApiResponse(List.of(), "OK", "req", 0, null);
        when(tickerClient.fetchPage(eq("ticker"), eq("asc"), eq(10), eq(null))).thenReturn(emptyResp);

        importService.fetchInstruments(SortBy.ticker, OrderDir.asc, 10);

        verify(writer, never()).persistPage(any(), any(SyncState.class));
        verify(syncStateRepository).findTopBySortByAndSortDirOrderByIdDesc("ticker", "asc");
    }

    @Test
    @DisplayName("Should fetch instruments, then persist instruments and sync state")
    void fetchInstruments_withResults_callsWriterAndPassesCorrectState() {
        SyncState lastState = new SyncState(5, "asc", "ticker", "next-url");
        when(syncStateRepository.findTopBySortByAndSortDirOrderByIdDesc(eq("name"), eq("desc")))
                .thenReturn(Optional.of(lastState));

        List<TickerDTO> tickers = List.of(createTicker("AAA"), createTicker("BBB"));
        TickerApiResponse resp = new TickerApiResponse(tickers, "OK", "req2", tickers.size(), "next-url2");

        when(tickerClient.fetchPage(eq("name"), eq("desc"), eq(50), eq(lastState.getNextUrl()))).thenReturn(resp);

        when(writer.persistPage(any(), any())).thenReturn(2);

        importService.fetchInstruments(SortBy.name, OrderDir.desc, 50);

        ArgumentCaptor<List<Instrument>> listCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<SyncState> stateCaptor = ArgumentCaptor.forClass(SyncState.class);

        verify(writer, times(1)).persistPage(listCaptor.capture(), stateCaptor.capture());

        List<Instrument> instruments = listCaptor.getValue();
        SyncState state = stateCaptor.getValue();

        assertThat(instruments).hasSize(2);
        assertThat(instruments.getFirst().getTicker()).isEqualTo("AAA");
        assertThat(instruments.getLast().getTicker()).isEqualTo("BBB");

        assertThat(state.getItems()).isEqualTo(2);
        assertThat(state.getSortDir()).isEqualTo(OrderDir.desc.name());
        assertThat(state.getSortBy()).isEqualTo(SortBy.name.name());
        assertThat(state.getNextUrl()).isEqualTo(resp.next_url());
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
