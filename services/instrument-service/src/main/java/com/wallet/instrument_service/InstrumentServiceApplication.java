package com.wallet.instrument_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class InstrumentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstrumentServiceApplication.class, args);
	}

	@Bean
	RestClient tickerRestClient(RestClient.Builder builder,
								@Value("${app.ticker-api.base-url}") String url){
		return builder.baseUrl(url).build();
	}
}
