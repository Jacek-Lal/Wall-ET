CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE instrument (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticker TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    market TEXT NOT NULL CHECK (market IN ('STOCKS', 'CRYPTO', 'FX', 'OTC', 'INDICES')),
    primary_exchange TEXT,
    currency_symbol VARCHAR(3),
    base_currency_symbol TEXT,
    type TEXT,
    cik VARCHAR(10) CHECK (cik ~ '^[0-9]*$' OR cik IS NULL),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ix_instrument_ticker_trgm ON instrument USING GIST(ticker gist_trgm_ops(siglen=64));
CREATE INDEX ix_instrument_name_trgm ON instrument USING GIST(name gist_trgm_ops(siglen=64));

CREATE TABLE instrument_sync_state (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    items INT NOT NULL CHECK (items > 0),
    market TEXT NOT NULL CHECK (market IN ('STOCKS', 'CRYPTO', 'FX', 'OTC', 'INDICES')),
    order_dir VARCHAR(4) NOT NULL CHECK (order_dir IN ('ASC', 'DESC')),
    sort_by TEXT NOT NULL,
    next_url TEXT NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

