package com.wallet.portfolio_service.core.persistence.repository;

import com.wallet.portfolio_service.core.persistence.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
}
