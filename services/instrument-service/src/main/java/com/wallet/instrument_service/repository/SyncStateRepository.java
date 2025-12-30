package com.wallet.instrument_service.repository;

import com.wallet.instrument_service.model.SyncState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SyncStateRepository extends JpaRepository<SyncState, Integer> {
    @Query("""
            SELECT s FROM SyncState s
            WHERE s.sortBy = :sort AND s.sortDir = :order
            ORDER BY s.id DESC
            FETCH FIRST 1 ROWS ONLY
           """)
    Optional<SyncState> findLastStateBySortAndDir(@Param("sort") String sort, @Param("order") String order);
}
