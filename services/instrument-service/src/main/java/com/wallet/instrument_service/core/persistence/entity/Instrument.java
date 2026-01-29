package com.wallet.instrument_service.core.persistence.entity;

import com.wallet.instrument_service.core.api.dto.InstrumentCreateRequest;
import com.wallet.instrument_service.core.integration.dto.TickerDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private String ticker;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String exchange;

    @Column(nullable = false, length = 2)
    private String country;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private String market;

    @Column(name = "asset_type")
    private String assetType;

    @Column
    private String cik;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Instrument(InstrumentCreateRequest request) {
        this.ticker = request.ticker();
        this.name = request.name();
        this.exchange = request.exchange();
        this.country = request.country();
        this.currency = request.currency();
        this.market = request.market();
        this.assetType = request.asset_type();
        this.cik = request.cik();
    }
    public Instrument(TickerDTO tickerDTO){
        this.ticker = tickerDTO.ticker();
        this.name = tickerDTO.name();
        this.exchange = tickerDTO.primary_exchange();
        this.country = tickerDTO.locale();
        this.currency = tickerDTO.currency_name();
        this.market = tickerDTO.market();
        this.assetType = tickerDTO.type();
        this.cik = tickerDTO.cik();
    }
}
