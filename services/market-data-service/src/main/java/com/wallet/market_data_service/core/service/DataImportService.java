package com.wallet.market_data_service.core.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.wallet.market_data_service.core.integration.StooqApiClient;
import com.wallet.market_data_service.core.integration.dto.PriceBar;
import com.wallet.market_data_service.core.persistence.dao.MarketDataWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.wallet.market_data_service.core.persistence.dao.MarketDataWriter.BATCH_SIZE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataImportService {

    private final StooqApiClient apiClient;
    private final MarketDataWriter marketDataWriter;

    public void fetchTickerData(String ticker) {
        Resource response = apiClient.fetchCsv(ticker);

        try (var reader = new InputStreamReader(response.getInputStream(), StandardCharsets.UTF_8)) {
            int inserted = 0;
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            MappingIterator<PriceBar> it = mapper.readerFor(PriceBar.class)
                    .with(schema)
                    .readValues(reader);

            List<PriceBar> buffer = new ArrayList<>(BATCH_SIZE);

            while (it.hasNextValue()){
                PriceBar pb = it.nextValue();

                if(isValidPriceBar(pb))
                    buffer.add(pb);

                if(buffer.size() == BATCH_SIZE){
                    inserted += marketDataWriter.batchUpsertTickerPrices(ticker, buffer);
                    buffer.clear();
                }
            }

            if(!buffer.isEmpty()){
                inserted += marketDataWriter.batchUpsertTickerPrices(ticker, buffer);
            }

            log.info("Prices inserted: {}, skipped: {}", inserted, buffer.size() - inserted);

        } catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalStateException("Failed to parse Stooq CSV");
        }
    }

    private boolean isValidPriceBar(PriceBar pb){
        return pb.date() != null && pb.close() != null;
    }
}
