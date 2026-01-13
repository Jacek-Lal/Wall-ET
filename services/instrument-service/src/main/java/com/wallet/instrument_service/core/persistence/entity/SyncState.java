package com.wallet.instrument_service.core.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false, name = "sort_dir")
    private String sortDir;

    @Column(nullable = false, name = "sort_by")
    private String sortBy;

    @Column(nullable = false, unique = true, name = "next_url")
    private String nextUrl;

    public SyncState(int items, String sortDir, String sortBy, String nextUrl){
        this.items = items;
        this.sortDir = sortDir;
        this.sortBy = sortBy;
        this.nextUrl = nextUrl;
    }
}
