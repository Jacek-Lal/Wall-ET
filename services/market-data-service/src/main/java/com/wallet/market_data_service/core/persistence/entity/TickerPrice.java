package com.wallet.market_data_service.core.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "prices_daily")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TickerPrice {

    @Embeddable
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Id implements Serializable {
        private String ticker;
        private LocalDate day;
    }

    @EmbeddedId
    private Id id;

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;

    @Column(nullable = false)
    private BigDecimal close;
    private BigDecimal volume;

    public TickerPrice(String ticker, LocalDate day, BigDecimal open, BigDecimal high,
                       BigDecimal low, BigDecimal close, BigDecimal volume){
        this.id = new Id(ticker, day);
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
}
