package com.wallet.instrument_service.core.persistence.entity;

import com.wallet.instrument_service.core.api.enums.OrderDir;
import com.wallet.instrument_service.core.api.enums.SortBy;
import com.wallet.instrument_service.core.persistence.enums.Market;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "instrument_sync_state")
@Getter
@NoArgsConstructor
public class SyncState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer items;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Market market;

    @Column(name = "order_dir", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderDir orderDir;

    @Column( name = "sort_by", nullable = false)
    @Enumerated(EnumType.STRING)
    private SortBy sortBy;

    @Column(name = "next_url", nullable = false, unique = true)
    private String nextUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public SyncState(int items, Market market, OrderDir orderDir, SortBy sortBy, String nextUrl){
        this.items = items;
        this.market = market;
        this.orderDir = orderDir;
        this.sortBy = sortBy;
        this.nextUrl = nextUrl;
    }
}
