package com.wallet.market_data_service.core.persistence.repository;

import com.wallet.market_data_service.core.persistence.entity.TickerPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TickerPriceRepository extends JpaRepository<TickerPrice, TickerPrice.Id> {
    List<TickerPrice> findAllByIdTickerAndIdDayBetween(String ticker, LocalDate from, LocalDate to);
}
