package com.wallet.portfolio_service.core.service;

import com.wallet.portfolio_service.core.api.dto.PortfolioRequest;
import com.wallet.portfolio_service.core.api.dto.PortfolioResponse;
import com.wallet.portfolio_service.core.mapper.PortfolioMapper;
import com.wallet.portfolio_service.core.persistence.entity.Portfolio;
import com.wallet.portfolio_service.core.persistence.repository.PortfolioRepository;
import com.wallet.portfolio_service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;

    public PortfolioResponse createPortfolio(PortfolioRequest request) {
        Portfolio portfolio = portfolioRepository.save(portfolioMapper.toEntity(request));

        return portfolioMapper.toResponse(portfolio);
    }

    public PortfolioResponse getPortfolio(UUID id) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Portfolio with id " + id + " does not exist")
        );

        return portfolioMapper.toResponse(portfolio);
    }

    public List<PortfolioResponse> getPortfolios() {
        return portfolioRepository.findAll().stream().map(portfolioMapper::toResponse).toList();
    }

    public void deletePortfolio(UUID id) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Portfolio with id " + id + " does not exist")
        );

        portfolioRepository.delete(portfolio);
    }

    public PortfolioResponse updatePortfolio(UUID id, PortfolioRequest request) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Portfolio with id " + id + " does not exist")
        );

        portfolio.setName(request.name());
        portfolio.setBaseCurrency(request.baseCurrency());

        Portfolio updated = portfolioRepository.save(portfolio);

        return portfolioMapper.toResponse(updated);
    }
}
