package com.wallet.instrument_service.service;

import com.wallet.instrument_service.model.Instrument;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InstrumentImportService {

    private final JdbcTemplate jdbcTemplate;

    public InstrumentImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void upsertIgnoreDuplicates(List<Instrument> instruments) {
        String sql = """
            INSERT INTO instrument (ticker, name, exchange, country, currency, market, asset_type, cik)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (ticker) DO NOTHING
            """;

        jdbcTemplate.batchUpdate(sql, instruments, 1000, (ps, i) -> {
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

