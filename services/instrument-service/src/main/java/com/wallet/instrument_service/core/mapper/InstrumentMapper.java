package com.wallet.instrument_service.core.mapper;

import com.wallet.instrument_service.core.api.dto.InstrumentRequest;
import com.wallet.instrument_service.core.api.dto.InstrumentResponse;
import com.wallet.instrument_service.core.integration.dto.TickerResponse;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {
    Instrument toEntity(InstrumentRequest request);
    InstrumentResponse toResponse(Instrument instrument);

    @Mapping(target = "market", qualifiedByName = "toMarket")
    @Mapping(target = "type", qualifiedByName = "toInstrumentType")
    @Mapping(target = "currencySymbol", ignore = true)
    Instrument toEntity(TickerResponse dto);

    @AfterMapping
    default void resolveCurrencySymbol(TickerResponse dto, @MappingTarget Instrument instrument) {
        if (dto.currencySymbol() != null) {
            instrument.setCurrencySymbol(dto.currencySymbol().toUpperCase());
        } else if (dto.currencyName() != null) {
            instrument.setCurrencySymbol(dto.currencyName().toUpperCase());
        }
    }

    @Named("toMarket")
    default Market toMarket(String market) {
        return Market.from(market);
    }

    @Named("toInstrumentType")
    default InstrumentType toInstrumentType(String type) {
        return InstrumentType.from(type);
    }
}
