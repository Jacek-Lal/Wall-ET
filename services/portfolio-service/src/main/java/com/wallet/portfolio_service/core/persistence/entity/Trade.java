package com.wallet.portfolio_service.core.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trade")
@NoArgsConstructor
@Getter
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "portfolio_id", nullable = false)
    @ManyToOne(optional = false)
    @Setter
    private Portfolio portfolio;

    @JoinColumn(name = "instrument_ticker", nullable = false)
    @ManyToOne(optional = false)
    @Setter
    private Instrument instrument;

    @Column(name = "trade_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private TradeType tradeType;

    @Column(nullable = false)
    @Setter
    private BigDecimal quantity;

    @Column(nullable = false)
    @Setter
    private BigDecimal price;

    @Column(name = "trade_currency", nullable = false)
    @Setter
    private String tradeCurrency;

    @Column(name = "trade_date", nullable = false)
    @Setter
    private Instant tradeDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

