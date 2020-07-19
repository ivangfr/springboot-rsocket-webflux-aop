package com.mycompany.movieclient.config;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
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
    RSocket rSocket() {
        return RSocketConnector.create()
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .connect(TcpClientTransport.create(movieServerProperties.getRsocket().getPort()))
                .block();
    }

    @Bean
    RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies) {
        return RSocketRequester.builder()
                .rsocketStrategies(rSocketStrategies)
                .connectTcp(movieServerProperties.getHost(), movieServerProperties.getRsocket().getPort())
                .block();
    }

}
