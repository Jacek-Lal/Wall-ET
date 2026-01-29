package com.wallet.instrument_service.core.persistence.repo;

import com.wallet.instrument_service.core.persistence.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {
}
