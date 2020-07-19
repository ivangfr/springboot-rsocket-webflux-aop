package com.mycompany.movieclient.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

@RequiredArgsConstructor
@Configuration
public class MovieServerRSocketConfig {

    private final MovieServerProperties movieServerProperties;

    @Bean
    RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies) {
        return RSocketRequester.builder()
                .rsocketStrategies(rSocketStrategies)
                .connectTcp(movieServerProperties.getHost(), movieServerProperties.getRsocket().getPort())
                .block();
    }

}
