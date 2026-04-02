package com.wallet.instrument_service.core.persistence.repo;

import com.wallet.instrument_service.core.api.enums.OrderDir;
import com.wallet.instrument_service.core.api.enums.SortBy;
import com.wallet.instrument_service.core.persistence.entity.SyncState;
import com.wallet.instrument_service.core.persistence.enums.Market;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SyncStateRepository extends JpaRepository<SyncState, Integer> {
    Optional<SyncState> findTopByMarketAndSortByAndOrderDirOrderByIdDesc(Market market, SortBy sort, OrderDir order);
}
