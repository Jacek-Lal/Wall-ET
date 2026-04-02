package com.wallet.instrument_service.core.service;

import com.wallet.instrument_service.core.api.dto.InstrumentRequest;
import com.wallet.instrument_service.core.api.dto.InstrumentResponse;
import com.wallet.instrument_service.core.mapper.InstrumentMapper;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import com.wallet.instrument_service.core.persistence.repo.InstrumentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstrumentServiceTest {

    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private InstrumentMapper instrumentMapper;

    @InjectMocks
    private InstrumentService instrumentService;

    @Test
    @DisplayName("createInstrument should save entity and return response with id and createdAt")
    void createInstrument_savesAndReturnsResponse() {
        InstrumentRequest req = new InstrumentRequest("AAPL", "Apple Inc.", Market.STOCKS, "XNYS",
                "USD", "US", InstrumentType.CS, "100300400");

        Instrument saved = new Instrument(req.ticker(), req.name(), req.market(), req.primaryExchange(),
                req.currencySymbol(), req.baseCurrencySymbol(), req.type(), req.cik());
        ReflectionTestUtils.setField(saved, "id", 123L);
        Instant now = Instant.now();
        ReflectionTestUtils.setField(saved, "createdAt", now);

        when(instrumentMapper.toEntity(any(InstrumentRequest.class))).thenReturn(saved);
        when(instrumentRepository.save(any(Instrument.class))).thenReturn(saved);
        when(instrumentMapper.toResponse(any(Instrument.class))).thenAnswer(inv -> {
            Instrument s = inv.getArgument(0);
            return new InstrumentResponse(s.getId(), s.getTicker(), s.getName(), s.getMarket(), s.getPrimaryExchange(),
                    s.getCurrencySymbol(), s.getBaseCurrencySymbol(), s.getType(), s.getCik(), s.getCreatedAt());
        });

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

        when(instrumentRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(instruments));
        when(instrumentMapper.toResponse(any(Instrument.class))).thenAnswer(inv -> {
            Instrument s = inv.getArgument(0);
            return new InstrumentResponse(s.getId(), s.getTicker(), s.getName(), s.getMarket(), s.getPrimaryExchange(),
                    s.getCurrencySymbol(), s.getBaseCurrencySymbol(), s.getType(), s.getCik(), s.getCreatedAt());
        });

        var result = instrumentService.getInstruments(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).ticker()).isEqualTo("AAA");
        assertThat(result.getContent().get(1).ticker()).isEqualTo("BBB");

        verify(instrumentRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("deleteInstruments should call repository.deleteAll")
    void deleteInstruments_callsRepositoryDeleteAll() {
        instrumentService.deleteInstruments();
        verify(instrumentRepository).deleteAll();
    }

    private Instrument createInstrument(String ticker) {
        return new Instrument(
                ticker, ticker + " Name", Market.STOCKS, "EX", "USD",
                "US", InstrumentType.CS, "100");
    }
}
