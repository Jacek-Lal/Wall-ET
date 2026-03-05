package com.wallet.instrument_service.unit;

import com.wallet.instrument_service.core.api.dto.InstrumentRequest;
import com.wallet.instrument_service.core.api.dto.InstrumentResponse;
import com.wallet.instrument_service.core.integration.dto.TickerDTO;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.repo.InstrumentRepository;
import com.wallet.instrument_service.core.service.InstrumentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstrumentServiceTest {

    @Mock InstrumentRepository instrumentRepository;

    @InjectMocks
    InstrumentService instrumentService;

    @Test
    @DisplayName("createInstrument should save entity and return response with id and createdAt")
    void createInstrument_savesAndReturnsResponse() {
        InstrumentRequest req = new InstrumentRequest("AAPL", "Apple Inc.", "XNYS",
                "US", "USD", "stocks", "CS", "100300400");

        Instrument saved = new Instrument(req);
        ReflectionTestUtils.setField(saved, "id", 123L);
        Instant now = Instant.now();
        ReflectionTestUtils.setField(saved, "createdAt", now);

        when(instrumentRepository.save(any(Instrument.class))).thenReturn(saved);

        InstrumentResponse res = instrumentService.createInstrument(req);

        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(123L);
        assertThat(res.ticker()).isEqualTo(req.ticker());
        assertThat(res.createdAt()).isEqualTo(now);

        verify(instrumentRepository).save(any(Instrument.class));
    }

    @Test
    @DisplayName("getAllInstruments should map entity list to request DTOs")
    void getAllInstruments_mapsEntitiesToRequests() {
        List<Instrument> instruments = List.of(createInstrument("AAA"), createInstrument("BBB"));

        when(instrumentRepository.findAll()).thenReturn(instruments);

        List<InstrumentRequest> result = instrumentService.getAllInstruments();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).ticker()).isEqualTo("AAA");
        assertThat(result.get(1).ticker()).isEqualTo("BBB");

        verify(instrumentRepository).findAll();
    }

    @Test
    @DisplayName("deleteInstruments should call repository.deleteAll")
    void deleteInstruments_callsRepositoryDeleteAll() {
        instrumentService.deleteInstruments();
        verify(instrumentRepository).deleteAll();
    }

    private Instrument createInstrument(String ticker) {
        return new Instrument(new TickerDTO(
                ticker,
                ticker + " Name",
                "Market",
                "EX",
                "Type",
                "USD",
                "CIK",
                "US"
        ));
    }
}
