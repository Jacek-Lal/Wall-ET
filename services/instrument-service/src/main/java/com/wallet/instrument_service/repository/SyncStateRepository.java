package com.wallet.instrument_service.repository;

import com.wallet.instrument_service.model.SyncState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncStateRepository extends JpaRepository<SyncState, Integer> {
    SyncState findTopByOrderByIdDesc();
}
