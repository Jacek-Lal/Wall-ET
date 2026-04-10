package com.wallet.portfolio_service.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "instrument_snapshot")
@Getter
@NoArgsConstructor
public class Instrument {

    @Id
    @Column(nullable = false, unique = true)
    @Setter
    private String ticker;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = false)
    @Setter
    private String market;

    @Column(name = "currency_symbol", length = 3)
    @Setter
    private String currencySymbol;

    @Column(name = "base_currency_symbol")
    @Setter
    private String baseCurrencySymbol;

    @Setter
    private String type;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
