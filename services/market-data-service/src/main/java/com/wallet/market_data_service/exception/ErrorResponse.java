package com.wallet.market_data_service.exception;

import java.time.Instant;

public record ErrorResponse(int status, String message, Instant timestamp) {
}
