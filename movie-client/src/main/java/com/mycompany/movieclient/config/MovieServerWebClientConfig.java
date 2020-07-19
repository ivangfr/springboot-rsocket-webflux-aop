package com.mycompany.movieclient.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Configuration
public class MovieServerWebClientConfig {

    private final MovieServerProperties movieServerProperties;

    @Bean
    WebClient webClient() {
        String movieServerRestUrl = String.format("http://%s:%s/api/movies",
                movieServerProperties.getHost(), movieServerProperties.getRest().getPort());
        return WebClient.create(movieServerRestUrl);
    }

}
