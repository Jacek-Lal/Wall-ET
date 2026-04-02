package com.wallet.instrument_service.core.service;

import com.wallet.instrument_service.core.api.enums.OrderDir;
import com.wallet.instrument_service.core.api.enums.SortBy;
import com.wallet.instrument_service.core.integration.TickerApiClient;
import com.wallet.instrument_service.core.integration.dto.FullResponse;
import com.wallet.instrument_service.core.mapper.InstrumentMapper;
import com.wallet.instrument_service.core.persistence.dao.InstrumentSyncWriter;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.entity.SyncState;
import com.wallet.instrument_service.core.persistence.enums.Market;
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
    private final InstrumentMapper instrumentMapper;

    public void fetchInstruments(Market market, SortBy sort, OrderDir order, int limit) {
        SyncState lastState = syncStateRepository
                .findTopByMarketAndSortByAndOrderDirOrderByIdDesc(market, sort, order)
                .orElse(null);

        FullResponse response;
        if (lastState == null){
            response = tickerClient.fetch(
                    market.name().toLowerCase(),
                    sort.name().toLowerCase(),
                    order.name().toLowerCase(),
                    limit);
        } else {
            response = tickerClient.fetchNext(lastState.getNextUrl());
        }

        if (response.count() == 0){
            log.info("No instruments in response");
            return;
        }

        List<Instrument> instruments = response
                .results()
                .stream()
                .map(instrumentMapper::toEntity)
                .toList();

        SyncState newState = new SyncState(response.count(), market, order, sort, response.nextUrl());

        int inserted = writer.persistPage(instruments, newState);

        log.info("Import finished: fetched={}, inserted={}, skipped={}",
                instruments.size(), inserted, instruments.size() - inserted);
    }
}
