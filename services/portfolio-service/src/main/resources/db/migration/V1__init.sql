CREATE TABLE portfolio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    base_currency CHAR(3) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE trade (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    portfolio_id BIGINT NOT NULL REFERENCES portfolio(id) ON DELETE CASCADE,
    instrument_id BIGINT NOT NULL,
    trade_type TEXT NOT NULL CHECK (trade_type IN ('BUY', 'SELL')),
    quantity NUMERIC(20, 6) NOT NULL CHECK (quantity > 0),
    price NUMERIC(20, 6) NOT NULL CHECK (price > 0),
    trade_date TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ix_trades_portfolio_time
  ON trade (portfolio_id, trade_date DESC);

CREATE INDEX ix_trades_portfolio_instrument
  ON trade (portfolio_id, instrument_id);

CREATE TABLE price_latest (
  instrument_id BIGINT PRIMARY KEY,
  price         NUMERIC(20, 8) NOT NULL,
  currency      CHAR(3) NOT NULL,
  as_of         TIMESTAMPTZ NOT NULL,
  source        TEXT NOT NULL
);

CREATE TABLE fx_rate_latest (
  base_currency  CHAR(3) NOT NULL,
  quote_currency CHAR(3) NOT NULL,
  rate           NUMERIC(20, 10) NOT NULL,
  as_of          TIMESTAMPTZ NOT NULL,
  source         TEXT NOT NULL,
  PRIMARY KEY (base_currency, quote_currency)
);


