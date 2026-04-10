package com.wallet.portfolio_service.core.mapper;

import com.wallet.portfolio_service.core.api.dto.TradeRequest;
import com.wallet.portfolio_service.core.api.dto.TradeResponse;
import com.wallet.portfolio_service.core.persistence.entity.Trade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {InstrumentMapper.class})
public interface TradeMapper {
    Trade toEntity(TradeRequest request);

    @Mapping(target = "ticker", source = "instrument.ticker")
    TradeResponse toResponse(Trade entity);
}
