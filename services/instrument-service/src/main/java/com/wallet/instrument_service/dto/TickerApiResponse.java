package com.wallet.instrument_service.dto;

import java.util.List;

public record TickerApiResponse(
        List<TickerDTO> results,
        String status,
        String request_id,
        int count,
        String next_url
) {
}
