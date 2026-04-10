package com.wallet.portfolio_service.core.persistence.repository;

import com.wallet.portfolio_service.core.persistence.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TradeRepository extends JpaRepository<Trade, UUID> {
    Optional<Trade> findByPortfolio_IdAndId(UUID portfolioId, UUID id);
    List<Trade> findAllByPortfolio_Id(UUID portfolioId);
}
