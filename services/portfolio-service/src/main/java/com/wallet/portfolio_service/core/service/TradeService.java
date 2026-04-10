package com.wallet.portfolio_service.core.service;

import com.wallet.portfolio_service.core.api.dto.TradeRequest;
import com.wallet.portfolio_service.core.api.dto.TradeResponse;
import com.wallet.portfolio_service.core.integration.InstrumentServiceClient;
import com.wallet.portfolio_service.core.integration.dto.InstrumentResponse;
import com.wallet.portfolio_service.core.mapper.InstrumentMapper;
import com.wallet.portfolio_service.core.mapper.TradeMapper;
import com.wallet.portfolio_service.core.persistence.entity.Instrument;
import com.wallet.portfolio_service.core.persistence.entity.Portfolio;
import com.wallet.portfolio_service.core.persistence.entity.Trade;
import com.wallet.portfolio_service.core.persistence.repository.InstrumentRepository;
import com.wallet.portfolio_service.core.persistence.repository.PortfolioRepository;
import com.wallet.portfolio_service.core.persistence.repository.TradeRepository;
import com.wallet.portfolio_service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeService {

    private final TradeRepository tradeRepository;
    private final PortfolioRepository portfolioRepository;
    private final InstrumentRepository instrumentRepository;
    private final InstrumentMapper instrumentMapper;
    private final TradeMapper tradeMapper;
    private final InstrumentServiceClient instrumentServiceClient;

    public TradeResponse createTrade(UUID portfolioId, TradeRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() ->
                new ResourceNotFoundException("Portfolio with id " + portfolioId + " does not exist")
        );

        Instrument instrument = instrumentRepository.findByTicker(request.ticker())
                .orElseGet(()-> {
                    InstrumentResponse response = instrumentServiceClient.getByTicker(request.ticker());
                    Instrument entity = instrumentMapper.toEntity(response);
                    log.info("Saving instrument snapshot: ticker={}, name={}", entity.getTicker(), entity.getName());
                    return instrumentRepository.save(entity);
                });

        Trade trade = tradeMapper.toEntity(request);
        trade.setPortfolio(portfolio);
        trade.setInstrument(instrument);

        Trade saved = tradeRepository.save(trade);
        return tradeMapper.toResponse(saved);
    }

    public TradeResponse getTrade(UUID portfolioId, UUID tradeId) {
        Trade trade = tradeRepository.findByPortfolio_IdAndId(portfolioId, tradeId).orElseThrow(() ->
                new ResourceNotFoundException("Trade with id " + tradeId + " does not exist"));

        return tradeMapper.toResponse(trade);
    }

    public List<TradeResponse> getTrades(UUID portfolioId) {
        return tradeRepository.findAllByPortfolio_Id(portfolioId).stream().map(tradeMapper::toResponse).toList();
    }

    public void deleteTrade(UUID portfolioId, UUID tradeId) {
        Trade trade = tradeRepository.findByPortfolio_IdAndId(portfolioId, tradeId).orElseThrow(() ->
                new ResourceNotFoundException("Trade with id " + tradeId + " does not exist"));

        tradeRepository.delete(trade);
    }
}
