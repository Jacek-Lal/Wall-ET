package com.wallet.instrument_service.core.mapper;


import com.wallet.instrument_service.core.integration.dto.TickerResponse;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class InstrumentMapperTest {

    private final InstrumentMapper mapper = Mappers.getMapper(InstrumentMapper.class);

    @Test
    @DisplayName("Should use currencySymbol over currencyName when both are present")
    void shouldPrioritizeCurrencySymbolOverCurrencyName() {
        TickerResponse dto = new TickerResponse(
                "X:BTCUSD", "Bitcoin", "crypto", null, null,
                "USD", "United States dollar", "BTC", null, true
        );

        assertThat(mapper.toEntity(dto).getCurrencySymbol()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should resolve currencySymbol from currencyName when currencySymbol is absent")
    void shouldFallbackToCurrencyNameWhenSymbolAbsent() {
        TickerResponse dto = new TickerResponse(
                "AAPL", "Apple", "stocks", "XNAS", "CS",
                null, "usd", null, null, true
        );

        assertThat(mapper.toEntity(dto).getCurrencySymbol()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should leave currencySymbol null when both currencySymbol and currencyName are absent")
    void shouldLeaveCurrencySymbolNullWhenBothAbsent() {
        TickerResponse dto = new TickerResponse(
                "I:SPX", "S&P 500", "indices", null, null,
                null, null, null, null, true
        );

        assertThat(mapper.toEntity(dto).getCurrencySymbol()).isNull();
    }

    @Test
    @DisplayName("Should map unrecognized instrument type to OTHER")
    void shouldMapUnknownInstrumentTypeToOther() {
        TickerResponse dto = new TickerResponse(
                "AAPL", "Apple", "stocks", "XNAS", "UNKNOWN",
                null, "usd", null, null, true
        );

        assertThat(mapper.toEntity(dto).getType()).isEqualTo(InstrumentType.OTHER);
    }

    @Test
    @DisplayName("Should leave instrument type null when type is absent in response")
    void shouldHandleNullInstrumentType() {
        TickerResponse dto = new TickerResponse(
                "X:BTCUSD", "Bitcoin", "crypto", null, null,
                "USD", null, "BTC", null, true
        );

        assertThat(mapper.toEntity(dto).getType()).isNull();
    }

    @Test
    @DisplayName("Should map lowercase market string to Market enum")
    void shouldMapMarketCaseInsensitive() {
        TickerResponse dto = new TickerResponse(
                "AAPL", "Apple", "stocks", null, null,
                null, null, null, null, true
        );

        assertThat(mapper.toEntity(dto).getMarket()).isEqualTo(Market.STOCKS);
    }
}