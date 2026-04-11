package com.wallet.portfolio_service.core.mapper;

import com.wallet.portfolio_service.core.api.dto.PortfolioRequest;
import com.wallet.portfolio_service.core.api.dto.PortfolioResponse;
import com.wallet.portfolio_service.core.persistence.entity.Portfolio;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {
    Portfolio toEntity(PortfolioRequest request);
    PortfolioResponse toResponse(Portfolio entity);
    void updateEntity(PortfolioRequest request, @MappingTarget Portfolio entity);

}
