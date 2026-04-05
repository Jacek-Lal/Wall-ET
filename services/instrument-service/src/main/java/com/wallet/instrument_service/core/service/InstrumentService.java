package com.wallet.instrument_service.core.service;

import com.wallet.instrument_service.core.api.dto.InstrumentRequest;
import com.wallet.instrument_service.core.api.dto.InstrumentResponse;
import com.wallet.instrument_service.core.mapper.InstrumentMapper;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.repo.InstrumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final InstrumentMapper instrumentMapper;

    public InstrumentResponse createInstrument(InstrumentRequest request) {
        Instrument instrument = instrumentRepository.save(instrumentMapper.toEntity(request));
        return instrumentMapper.toResponse(instrument);
    }

    public Page<InstrumentResponse> getInstruments(Pageable pageable) {
        return instrumentRepository.findAll(pageable).map(instrumentMapper::toResponse);
    }

    public List<InstrumentResponse> search(String query) {
        if (query == null || query.isBlank()) return List.of();
        return instrumentRepository.search(query.trim())
                .stream()
                .map(instrumentMapper::toResponse)
                .toList();
    }

    public void deleteInstruments() {
        instrumentRepository.deleteAll();
        log.info("Instruments wiped");
    }
}
