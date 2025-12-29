package com.wallet.instrument_service.model;

import com.wallet.instrument_service.dto.InstrumentDTO;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "instrument")
@Getter
public class Instrument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Instrument(InstrumentDTO request) {
        this.ticker = request.ticker();
        this.name = request.name();
        this.exchange = request.exchange();
        this.country = request.country();
        this.currency = request.currency();
        this.market = request.market();
        this.assetType = request.asset_type();
        this.cik = request.cik();
    }
}
