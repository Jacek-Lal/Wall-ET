CREATE TABLE instrument (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticker TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    exchange TEXT NOT NULL,
    country VARCHAR(2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    market TEXT NOT NULL,
    asset_type TEXT,
    cik VARCHAR(10) check (cik ~ '^[0-9]*$' OR cik IS NULL),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_instrument_exchange_ticker
  ON instrument (exchange, lower(ticker));

CREATE INDEX ix_instrument_ticker_lower
  ON instrument (lower(ticker));

CREATE INDEX ix_instrument_name_lower
  ON instrument (lower(name));

CREATE TABLE instrument_sync_state (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    items INT NOT NULL check (items > 0),
    sort_dir VARCHAR(4) NOT NULL check (sort_dir IN ('asc', 'desc')),
    sort_by TEXT NOT NULL,
    next_url TEXT NOT NULL UNIQUE
);

