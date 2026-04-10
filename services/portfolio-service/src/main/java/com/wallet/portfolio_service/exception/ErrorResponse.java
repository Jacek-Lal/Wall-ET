package com.wallet.portfolio_service.exception;

import java.time.Instant;

public record ErrorResponse(int status, String message, Instant timestamp) {
}
