package com.wallet.instrument_service.core.service;

import com.wallet.instrument_service.core.api.dto.InstrumentCreateRequest;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.repo.InstrumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;

    public void createInstrument(InstrumentCreateRequest request) {
        Instrument instrument = new Instrument(request);
        instrumentRepository.save(instrument);
    }

    public List<InstrumentCreateRequest> getAllInstruments() {
        List<Instrument> instruments = instrumentRepository.findAll();
        return instruments.stream()
                .map(instrument -> new InstrumentCreateRequest(
                        instrument.getTicker(),
                        instrument.getName(),
                        instrument.getExchange(),
                        instrument.getCountry(),
                        instrument.getCurrency(),
                        instrument.getMarket(),
                        instrument.getAssetType(),
                        instrument.getCik()
                ))
                .toList();
    }

    public void deleteInstruments() {
        instrumentRepository.deleteAll();
        log.info("Instruments wiped");
    }
}
