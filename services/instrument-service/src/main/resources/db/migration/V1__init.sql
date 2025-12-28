CREATE TABLE instrument(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticker TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    exchange TEXT NOT NULL,
    country CHAR(2) NOT NULL,
    currency CHAR(3) NOT NULL,
    stooq_symbol TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_instrument_exchange_ticker
  ON instrument (exchange, lower(ticker));

CREATE INDEX ix_instrument_ticker_lower
  ON instrument (lower(ticker));

CREATE INDEX ix_instrument_name_lower
  ON instrument (lower(name));