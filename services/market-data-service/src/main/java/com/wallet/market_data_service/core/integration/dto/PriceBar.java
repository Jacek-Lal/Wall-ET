package com.wallet.market_data_service.core.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.Date;

public record PriceBar(@JsonProperty("Date") Date date,
                       @JsonProperty("Open") BigDecimal open,
                       @JsonProperty("High") BigDecimal high,
                       @JsonProperty("Low") BigDecimal low,
                       @JsonProperty("Close") BigDecimal close,
                       @JsonProperty("Volume") BigDecimal volume) {
}
