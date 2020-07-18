package com.mycompany.movieclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;

@Configuration
public class RSocketConfig {

    @Value("${app.movie-server.rsocket.host}")
    private String movieServerHost;

    @Value("${app.movie-server.rsocket.port}")
    private Integer movieServerPort;

    @Bean
    public RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder.connectTcp(movieServerHost, movieServerPort).block();
    }

}
