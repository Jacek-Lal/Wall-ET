package com.wallet.instrument_service.core.persistence.repo;

import com.wallet.instrument_service.core.persistence.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {
    Optional<Instrument> findByTicker(String ticker);

    @Query(value = """
            SELECT * FROM instrument
            WHERE :query <% ticker OR :query <% name
            ORDER BY LEAST(ticker <<-> :query, name <<-> :query) ASC
            LIMIT 10
        """, nativeQuery = true)
    List<Instrument> search(@Param("query") String query);
}
