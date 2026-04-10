CREATE TABLE portfolio (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    base_currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE instrument_snapshot (
    ticker TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    market TEXT NOT NULL CHECK (market IN ('STOCKS', 'CRYPTO', 'FX', 'OTC', 'INDICES')),
    currency_symbol VARCHAR(3),
    base_currency_symbol TEXT,
    type TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE trade (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    portfolio_id UUID NOT NULL REFERENCES portfolio(id) ON DELETE CASCADE,
    instrument_ticker TEXT NOT NULL REFERENCES instrument_snapshot(ticker),
    trade_type TEXT NOT NULL CHECK (trade_type IN ('BUY', 'SELL')),
    quantity NUMERIC(20, 6) NOT NULL CHECK (quantity > 0),
    price NUMERIC(20, 6) NOT NULL CHECK (price > 0),
    trade_currency VARCHAR(3) NOT NULL,
    trade_date TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ix_trades_portfolio_time
  ON trade (portfolio_id, trade_date DESC);

CREATE INDEX ix_trades_portfolio_instrument
  ON trade (portfolio_id, instrument_ticker);

CREATE TABLE price_snapshot (
  instrument_ticker TEXT PRIMARY KEY REFERENCES instrument_snapshot(ticker),
  price         NUMERIC(20, 8) NOT NULL,
  as_of         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE fx_rate_snapshot (
  base_currency  VARCHAR(3) NOT NULL,
  quote_currency VARCHAR(3) NOT NULL,
  rate           NUMERIC(20, 10) NOT NULL,
  as_of          TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (base_currency, quote_currency)
);


