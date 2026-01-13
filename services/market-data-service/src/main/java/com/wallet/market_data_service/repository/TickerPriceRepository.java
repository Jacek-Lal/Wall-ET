package com.wallet.market_data_service.repository;

import com.wallet.market_data_service.model.TickerPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TickerPriceRepository extends JpaRepository<TickerPrice, TickerPrice.Id> {
}
