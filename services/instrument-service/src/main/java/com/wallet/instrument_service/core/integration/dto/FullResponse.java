package com.wallet.instrument_service.core.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FullResponse(List<TickerResponse> results,
                           String status,
                           @JsonProperty("request_id") String requestId,
                           int count,
                           @JsonProperty("next_url") String nextUrl) {}