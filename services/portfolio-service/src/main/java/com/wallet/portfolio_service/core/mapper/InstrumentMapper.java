package com.wallet.portfolio_service.core.mapper;

import com.wallet.portfolio_service.core.integration.dto.InstrumentResponse;
import com.wallet.portfolio_service.core.persistence.entity.Instrument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {
    Instrument toEntity(InstrumentResponse request);
}
