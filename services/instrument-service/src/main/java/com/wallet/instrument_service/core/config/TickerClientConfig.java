package com.wallet.instrument_service.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TickerClientConfig {

    @Bean
    RestClient tickerRestClient(RestClient.Builder builder,
                                @Value("${app.ticker-api.base-url}") String url){
        return builder.baseUrl(url).build();
    }
}
