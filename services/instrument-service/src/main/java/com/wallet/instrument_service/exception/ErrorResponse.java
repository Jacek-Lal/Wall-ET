package com.wallet.instrument_service.exception;

import java.time.Instant;

public record ErrorResponse(int status, String message, Instant timestamp) {
}
