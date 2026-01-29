package com.wallet.instrument_service.core.service;

import com.wallet.instrument_service.core.api.dto.InstrumentRequest;
import com.wallet.instrument_service.core.api.dto.InstrumentResponse;
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

    public InstrumentResponse createInstrument(InstrumentRequest request) {
        Instrument instrument = instrumentRepository.save(new Instrument(request));
        return new InstrumentResponse(instrument);
    }

    public List<InstrumentRequest> getAllInstruments() {
        List<Instrument> instruments = instrumentRepository.findAll();
        return instruments.stream()
                .map(instrument -> new InstrumentRequest(
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
