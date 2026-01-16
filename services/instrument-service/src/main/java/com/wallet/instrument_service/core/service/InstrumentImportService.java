package com.wallet.instrument_service.core.service;

import com.wallet.instrument_service.core.integration.TickerApiClient;
import com.wallet.instrument_service.core.integration.dto.TickerApiResponse;
import com.wallet.instrument_service.core.persistence.dao.InstrumentSyncWriter;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.entity.SyncState;
import com.wallet.instrument_service.core.persistence.repo.SyncStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentImportService {

    private final TickerApiClient tickerClient;
    private final SyncStateRepository syncStateRepository;
    private final InstrumentSyncWriter writer;

    public void fetchInstruments(String sort, String order, int limit) {
        SyncState lastState = syncStateRepository.findLastStateBySortAndDir(sort, order).orElse(null);
        String nextUrl = lastState != null ? lastState.getNextUrl() : null;

        TickerApiResponse response = tickerClient.fetchPage(sort, order, limit, nextUrl);

        List<Instrument> instruments = response
                .results()
                .stream()
                .map(Instrument::new)
                .toList();

        SyncState newState = new SyncState(response.count(), order, sort, response.next_url());

        int inserted = writer.persistPage(instruments, newState);

        log.info("Import finished: fetched={}, inserted={}, skipped={}",
                instruments.size(), inserted, instruments.size() - inserted);
    }
}
