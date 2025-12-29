package com.wallet.instrument_service.service;

import com.wallet.instrument_service.dto.InstrumentDTO;
import com.wallet.instrument_service.model.Instrument;
import com.wallet.instrument_service.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;

    public void createInstrument(InstrumentDTO request) {
        Instrument instrument = new Instrument(request);
        instrumentRepository.save(instrument);
    }

    public List<InstrumentDTO> getAllInstruments() {
        List<Instrument> instruments = instrumentRepository.findAll();
        return instruments.stream()
                .map(instrument -> new InstrumentDTO(
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
}
