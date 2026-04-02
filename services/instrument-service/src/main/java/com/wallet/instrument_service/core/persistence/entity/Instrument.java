package com.wallet.instrument_service.core.persistence.entity;

import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "instrument")
@Getter
@NoArgsConstructor
public class Instrument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Setter
    private String ticker;

    @Column(nullable = false)
    @Setter
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private Market market;

    @Column(name = "primary_exchange")
    @Setter
    private String primaryExchange;

    @Column(name = "currency_symbol", length = 3)
    @Setter
    private String currencySymbol;

    @Column(name = "base_currency_symbol")
    @Setter
    private String baseCurrencySymbol;

    @Enumerated(EnumType.STRING)
    @Setter
    private InstrumentType type;

    @Column
    @Setter
    private String cik;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Instrument(String ticker, String name, Market market, String primaryExchange, String currencySymbol,
                      String baseCurrencySymbol, InstrumentType type, String cik) {
        this.ticker = ticker;
        this.name = name;
        this.market = market;
        this.primaryExchange = primaryExchange;
        this.currencySymbol = currencySymbol;
        this.baseCurrencySymbol = baseCurrencySymbol;
        this.type = type;
        this.cik = cik;
    }
}
