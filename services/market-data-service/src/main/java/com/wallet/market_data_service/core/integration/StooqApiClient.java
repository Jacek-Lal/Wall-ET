package com.wallet.market_data_service.core.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class StooqApiClient {

    private final RestClient restClient;

    public Resource fetchCsv(String ticker){
        return restClient.get()
                .uri(builder -> builder
                        .queryParam("s", ticker)
                        .queryParam("i", "d")
                        .build())
                .retrieve()
                .body(Resource.class);
    }
}
