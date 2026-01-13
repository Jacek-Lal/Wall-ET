package com.wallet.market_data_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.OffsetDateTime;

@Entity
@Table(name = "prices_daily")
@Getter
public class TickerPrice {

    @Embeddable
    @Getter
    @AllArgsConstructor
    public static class Id implements Serializable {
        private String ticker;
        private Date day;
    }

    @EmbeddedId
    private Id id;

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;

    @Column(nullable = false)
    private BigDecimal close;
    private BigDecimal volume;

    @Column(length = 3)
    private String currency;

    @Column(nullable = false)
    private String source;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
