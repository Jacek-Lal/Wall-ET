package com.wallet.portfolio_service.core.integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class InstrumentClientConfig {
    @Bean
    RestClient instrumentRestClient(RestClient.Builder builder,
                                @Value("${app.instrument-service.base-url}") String url){
        return builder.baseUrl(url).build();
    }
}
