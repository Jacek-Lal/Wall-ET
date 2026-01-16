package com.wallet.market_data_service.core.persistence.dao;

import com.wallet.market_data_service.core.integration.dto.PriceBar;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketDataWriter {
    private final JdbcTemplate jdbcTemplate;
    public static final int BATCH_SIZE = 1000;

    @Transactional(rollbackOn = Exception.class)
    public int batchUpsertTickerPrices(String ticker, List<PriceBar> bars) {
        String sql = """
            INSERT INTO prices_daily (ticker, day, open, high, low, close, volume)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (ticker, day) DO NOTHING;
            """;

        int[][] result = jdbcTemplate.batchUpdate(sql, bars, BATCH_SIZE, (ps, pb) -> {
            ps.setString(1, ticker);
            ps.setObject(2, pb.date());
            ps.setBigDecimal(3, pb.open());
            ps.setBigDecimal(4, pb.high());
            ps.setBigDecimal(5, pb.low());
            ps.setBigDecimal(6, pb.close());
            ps.setObject(7, pb.volume());
        });

        return Arrays.stream(result).flatMapToInt(Arrays::stream).sum();
    }
}
