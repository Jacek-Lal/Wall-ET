package com.wallet.market_data_service.exception;

public class TickerNotFoundException extends RuntimeException {
    public TickerNotFoundException(String message) { super(message); }
}
