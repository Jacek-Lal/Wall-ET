package com.wallet.instrument_service.core.persistence.repo;

import com.wallet.instrument_service.config.TestcontainersConfig;
import com.wallet.instrument_service.core.persistence.entity.Instrument;
import com.wallet.instrument_service.core.persistence.enums.InstrumentType;
import com.wallet.instrument_service.core.persistence.enums.Market;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Import(TestcontainersConfig.class)
class InstrumentRepositoryTest {
    @Autowired
    private InstrumentRepository instrumentRepository;

    @BeforeEach
    void setUp() {
        instrumentRepository.deleteAll();
        instrumentRepository.saveAll(List.of(
                new Instrument("AAPL", "Apple Inc.", Market.STOCKS, "XNAS", "USD", null, InstrumentType.CS, null),
                new Instrument("MSFT", "Microsoft Corporation", Market.STOCKS, "XNAS", "USD", null, InstrumentType.CS, null),
                new Instrument("AAEQ", "Alpha Architect US Equity ETF", Market.STOCKS, "XNAS", "USD", null, InstrumentType.ETF, null),
                new Instrument("X:BTCUSD", "Bitcoin - United States dollar", Market.CRYPTO, null, "USD", "BTC", null, null)
        ));
    }

    @Test
    @DisplayName("Search should find instrument by ticker prefix")
    void search_findsByTickerPrefix() {
        List<Instrument> result = instrumentRepository.search("AAPL");

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getTicker()).isEqualTo("AAPL");
    }

    @Test
    @DisplayName("Search should find instrument by partial name word")
    void search_findsByPartialNameWord() {
        List<Instrument> result = instrumentRepository.search("alph");

        assertThat(result).isNotEmpty();
        assertThat(result).anyMatch(i -> i.getTicker().equals("AAEQ"));
    }

    @Test
    @DisplayName("Search should return max 10 results")
    void search_returnsMaxTenResults() {
        List<Instrument> bulk = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            bulk.add(new Instrument("ALPHA" + i, "Alpha Test Corp " + i, Market.STOCKS,
                    "XNAS", "USD", null, InstrumentType.CS, null));
        }
        instrumentRepository.saveAll(bulk);

        List<Instrument> result = instrumentRepository.search("alpha");

        assertThat(result).hasSizeLessThanOrEqualTo(10);
    }

    @Test
    @DisplayName("Search should return empty list when no match found")
    void search_returnsEmpty_whenNoMatch() {
        List<Instrument> result = instrumentRepository.search("zzzzzzzzz");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Search should be case insensitive")
    void search_isCaseInsensitive() {
        List<Instrument> upper = instrumentRepository.search("APPLE");
        List<Instrument> lower = instrumentRepository.search("apple");

        assertThat(upper).extracting(Instrument::getTicker)
                .containsExactlyInAnyOrderElementsOf(
                        lower.stream().map(Instrument::getTicker).toList());
    }

    @Test
    @DisplayName("Search should order results by best match first")
    void search_ordersByBestMatchFirst() {
        List<Instrument> result = instrumentRepository.search("apple");

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getTicker()).isEqualTo("AAPL");
    }
}