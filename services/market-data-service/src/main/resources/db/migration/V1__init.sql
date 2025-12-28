CREATE TABLE prices_daily (
  instrument_id BIGINT NOT NULL,
  day           DATE NOT NULL,

  open          NUMERIC(20, 8),
  high          NUMERIC(20, 8),
  low           NUMERIC(20, 8),
  close         NUMERIC(20, 8) NOT NULL,
  volume        NUMERIC(20, 2),

  currency      CHAR(3) NOT NULL,
  source        TEXT NOT NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

  PRIMARY KEY (instrument_id, day)
);

CREATE INDEX ix_prices_daily_instrument_day_desc
  ON prices_daily (instrument_id, day DESC);

CREATE TABLE fx_rates_daily (
  base_currency  CHAR(3) NOT NULL,
  quote_currency CHAR(3) NOT NULL,
  day            DATE NOT NULL,
  rate           NUMERIC(20, 10) NOT NULL,

  source         TEXT NOT NULL,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

  PRIMARY KEY (base_currency, quote_currency, day)
);

CREATE INDEX ix_fx_daily_pair_day_desc
  ON fx_rates_daily (base_currency, quote_currency, day DESC);

