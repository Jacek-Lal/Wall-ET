CREATE TABLE instrument (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticker TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    exchange TEXT NOT NULL,
    country CHAR(2) NOT NULL,
    currency CHAR(3) NOT NULL,
    market TEXT NOT NULL,
    asset_type TEXT,
    cik BIGINT check (cik > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_instrument_exchange_ticker
  ON instrument (exchange, lower(ticker));

CREATE INDEX ix_instrument_ticker_lower
  ON instrument (lower(ticker));

CREATE INDEX ix_instrument_name_lower
  ON instrument (lower(name));

CREATE TABLE instrument_sync_state (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    next_url TEXT NOT NULL UNIQUE,
    order CHAR(4) NOT NULL check (order IN ('asc', 'desc')),
    limit INT NOT NULL check (limit > 0),
    sort_by TEXT NOT NULL,
);

