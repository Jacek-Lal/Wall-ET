package com.wallet.instrument_service.core.persistence.repo;

import com.wallet.instrument_service.core.persistence.entity.SyncState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SyncStateRepository extends JpaRepository<SyncState, Integer> {
    Optional<SyncState> findTopBySortByAndSortDirOrderByIdDesc(String sort, String order);
}
