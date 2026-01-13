package com.wallet.instrument_service.core.service;

import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.entity.SyncState;
import com.wallet.instrument_service.core.persistence.repo.SyncStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentSyncWriter {

    private final JdbcTemplate jdbcTemplate;
    private final SyncStateRepository syncStateRepository;

    @Transactional(rollbackFor = Exception.class)
    public int persistPage(List<Instrument> instruments, SyncState newState) {

        int inserted = upsertIgnoreDuplicates(instruments);

        syncStateRepository.save(newState);
        log.info("Sync state saved: sort = {}, order = {}, nextUrl = {}",
                newState.getSortBy(), newState.getSortDir(), newState.getNextUrl());

        return inserted;
    }

    private int upsertIgnoreDuplicates(List<Instrument> instruments) {
        String sql = """
            INSERT INTO instrument (ticker, name, exchange, country, currency, market, asset_type, cik)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (ticker) DO NOTHING
            """;

        int[][] results = jdbcTemplate.batchUpdate(sql, instruments, 1000, (ps, i) -> {
            ps.setString(1, i.getTicker());
            ps.setString(2, i.getName());
            ps.setString(3, i.getExchange());
            ps.setString(4, i.getCountry());
            ps.setString(5, i.getCurrency());
            ps.setString(6, i.getMarket());
            ps.setString(7, i.getAssetType());
            ps.setString(8, i.getCik());
        });

        return Arrays.stream(results).flatMapToInt(Arrays::stream).sum();
    }
}
