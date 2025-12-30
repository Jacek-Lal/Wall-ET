package com.wallet.instrument_service.service;
import com.wallet.instrument_service.model.Instrument;
import com.wallet.instrument_service.model.SyncState;
import com.wallet.instrument_service.repository.SyncStateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class InstrumentImportTx {

    private final JdbcTemplate jdbcTemplate;
    private final SyncStateRepository syncStateRepository;

    public void saveSyncState(int items, String order, String sort, String nextUrl) {
        SyncState state = new SyncState(items, order, sort, nextUrl);
        syncStateRepository.save(state);
        log.info("Sync state saved: sort = {}, order = {}, nextUrl = {}", sort, order, nextUrl);
    }

    public void deleteSyncState(SyncState syncState){
        syncStateRepository.delete(syncState);
        log.info("Sync state deleted: id = {}, sort = {}, order = {}",
                syncState.getId(), syncState.getSortBy(), syncState.getSortDir());
    }

    public int[][] upsertIgnoreDuplicates(List<Instrument> instruments) {
        String sql = """
            INSERT INTO instrument (ticker, name, exchange, country, currency, market, asset_type, cik)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (ticker) DO NOTHING
            """;

        return jdbcTemplate.batchUpdate(sql, instruments, 1000, (ps, i) -> {
            ps.setString(1, i.getTicker());
            ps.setString(2, i.getName());
            ps.setString(3, i.getExchange());
            ps.setString(4, i.getCountry());
            ps.setString(5, i.getCurrency());
            ps.setString(6, i.getMarket());
            ps.setString(7, i.getAssetType());
            ps.setString(8, i.getCik());
        });
    }
}
