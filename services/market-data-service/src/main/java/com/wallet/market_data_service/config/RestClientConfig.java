package com.wallet.market_data_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient stooqRestClient(RestClient.Builder builder,
                                @Value("${stooq.api.base-url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
