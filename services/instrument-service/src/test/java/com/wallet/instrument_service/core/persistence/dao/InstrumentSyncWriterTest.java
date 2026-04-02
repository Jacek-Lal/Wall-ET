package com.wallet.instrument_service.core.persistence.dao;

import com.wallet.instrument_service.core.api.enums.OrderDir;
import com.wallet.instrument_service.core.api.enums.SortBy;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.entity.SyncState;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import com.wallet.instrument_service.core.persistence.repo.SyncStateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstrumentSyncWriterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private SyncStateRepository syncStateRepository;

    @InjectMocks
    private InstrumentSyncWriter writer;

    @Test
    @DisplayName("Should persist instruments and sync state, then return persisted instruments count")
    void shouldPersistInstrumentsAndSyncState() {
        List<Instrument> instruments = List.of(createInstrument("AAA"), createInstrument("BBB"));
        SyncState newState = new SyncState(2, Market.STOCKS, OrderDir.ASC, SortBy.TICKER, "next-url");

        int[][] results = new int[][]{{1, 1}, {1}};
        when(jdbcTemplate.batchUpdate(anyString(), eq(instruments), anyInt(), any())).thenReturn(results);

        int inserted = writer.persistPage(instruments, newState);

        assertThat(inserted).isEqualTo(3);
        verify(syncStateRepository).save(newState);
        verify(jdbcTemplate).batchUpdate(anyString(), eq(instruments), anyInt(), any());
    }

    @Test
    @DisplayName("Should persist only sync state given empty list of instruments and sync state")
    void shouldPersistSyncState() {
        List<Instrument> instruments = List.of();
        SyncState newState = new SyncState(0, Market.STOCKS, OrderDir.DESC, SortBy.NAME, "next-2");

        int[][] results = new int[][]{};
        when(jdbcTemplate.batchUpdate(anyString(), eq(instruments), anyInt(), any())).thenReturn(results);

        int inserted = writer.persistPage(instruments, newState);

        assertThat(inserted).isEqualTo(0);
        verify(syncStateRepository).save(newState);
    }

    private Instrument createInstrument(String ticker) {
        return new Instrument(
                ticker, ticker + " Name", Market.STOCKS, "EX", "USD",
                "US", InstrumentType.CS, "100");
    }
}