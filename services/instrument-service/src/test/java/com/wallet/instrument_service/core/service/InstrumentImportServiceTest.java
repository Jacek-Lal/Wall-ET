package com.wallet.instrument_service.core.service;

import com.wallet.instrument_service.core.api.enums.OrderDir;
import com.wallet.instrument_service.core.api.enums.SortBy;
import com.wallet.instrument_service.core.integration.TickerApiClient;
import com.wallet.instrument_service.core.integration.dto.FullResponse;
import com.wallet.instrument_service.core.integration.dto.TickerResponse;
import com.wallet.instrument_service.core.mapper.InstrumentMapper;
import com.wallet.instrument_service.core.persistence.dao.InstrumentSyncWriter;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.entity.SyncState;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import com.wallet.instrument_service.core.persistence.repo.SyncStateRepository;
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

    @Mock
    private TickerApiClient tickerClient;

    @Mock
    private SyncStateRepository syncStateRepository;

    @Mock
    private InstrumentSyncWriter writer;

    @Mock
    private InstrumentMapper instrumentMapper;

    @InjectMocks
    private InstrumentImportService importService;

    @Test
    @DisplayName("Should fetch instruments and don't persist page")
    void fetchInstruments_noResults_doesNotCallWriter() {
        when(syncStateRepository.findTopByMarketAndSortByAndOrderDirOrderByIdDesc(
                eq(Market.STOCKS), eq(SortBy.TICKER), eq(OrderDir.ASC))
        ).thenReturn(Optional.empty());

        FullResponse emptyResp = new FullResponse(List.of(), "OK", "req", 0, null);
        when(tickerClient.fetch(
                eq(Market.STOCKS.name().toLowerCase()),
                eq(SortBy.TICKER.name().toLowerCase()),
                eq(OrderDir.ASC.name().toLowerCase()), eq(10))
        ).thenReturn(emptyResp);

        importService.fetchInstruments(Market.STOCKS, SortBy.TICKER, OrderDir.ASC, 10);

        verify(writer, never()).persistPage(any(), any(SyncState.class));
        verify(syncStateRepository).findTopByMarketAndSortByAndOrderDirOrderByIdDesc(Market.STOCKS, SortBy.TICKER, OrderDir.ASC);
    }

    @Test
    @DisplayName("Should fetch instruments, then persist instruments and sync state")
    void fetchInstruments_withResults_callsWriterAndPassesCorrectState() {
        SyncState lastState = new SyncState(5, Market.STOCKS, OrderDir.DESC, SortBy.NAME, "next-url");
        when(syncStateRepository.findTopByMarketAndSortByAndOrderDirOrderByIdDesc(eq(Market.STOCKS), eq(SortBy.NAME), eq(OrderDir.DESC)))
                .thenReturn(Optional.of(lastState));

        List<TickerResponse> tickers = List.of(createTicker("AAA"), createTicker("BBB"));
        FullResponse resp = new FullResponse(tickers, "OK", "req2", tickers.size(), "next-url2");

        when(tickerClient.fetchNext(eq(lastState.getNextUrl()))).thenReturn(resp);

        when(writer.persistPage(any(), any())).thenReturn(2);
        when(instrumentMapper.toEntity(any(TickerResponse.class)))
                .thenAnswer(inv -> {
                    TickerResponse tr = inv.getArgument(0);
                    return new Instrument(tr.ticker(), tr.name(), Market.STOCKS, tr.primaryExchange(),
                            tr.currencySymbol(), tr.baseCurrencySymbol(), InstrumentType.CS, tr.cik());
                });

        importService.fetchInstruments(Market.STOCKS, SortBy.NAME, OrderDir.DESC, 50);

        ArgumentCaptor<List<Instrument>> listCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<SyncState> stateCaptor = ArgumentCaptor.forClass(SyncState.class);

        verify(writer, times(1)).persistPage(listCaptor.capture(), stateCaptor.capture());

        List<Instrument> instruments = listCaptor.getValue();
        SyncState state = stateCaptor.getValue();

        assertThat(instruments).hasSize(2);
        assertThat(instruments.get(0).getTicker()).isEqualTo("AAA");
        assertThat(instruments.get(1).getTicker()).isEqualTo("BBB");

        assertThat(state.getItems()).isEqualTo(2);
        assertThat(state.getOrderDir()).isEqualTo(OrderDir.DESC);
        assertThat(state.getSortBy()).isEqualTo(SortBy.NAME);
        assertThat(state.getNextUrl()).isEqualTo(resp.nextUrl());
    }

    private TickerResponse createTicker(String ticker) {
        return new TickerResponse(
                ticker, ticker + " Name", "Market", "EX", "Type",
                "USD", "USD_NAME", "US", "100", true);
    }
}
