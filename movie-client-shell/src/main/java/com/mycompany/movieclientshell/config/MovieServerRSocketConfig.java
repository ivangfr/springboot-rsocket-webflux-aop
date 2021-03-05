package com.mycompany.movieclientshell.config;

import io.rsocket.RSocket;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Configuration
public class MovieServerRSocketConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Profile("rsocket-tcp")
    @Bean
    RSocketRequester rSocketRequesterTcp(RSocketRequester.Builder rSocketRequesterBuilder,
                                         RSocketStrategies rSocketStrategies,
                                         @Value("${movie-server.host}") String host,
                                         @Value("${movie-server.rsocket.port}") int rSocketPort) {
        return createRSocketRequester(rSocketRequesterBuilder, rSocketStrategies,
                TcpClientTransport.create(host, rSocketPort));
    }

    @Profile("rsocket-websocket")
    @Bean
    RSocketRequester rSocketRequesterWebSocket(RSocketRequester.Builder rSocketRequesterBuilder,
                                               RSocketStrategies rSocketStrategies,
                                               @Value("${movie-server.host}") String host,
                                               @Value("${movie-server.rest.port}") int restPort,
                                               @Value("${movie-server.rsocket.mapping-path}") String rSocketMappingPath) {
        URI uri = URI.create(String.format("ws://%s:%s%s", host, restPort, rSocketMappingPath));
        return createRSocketRequester(rSocketRequesterBuilder, rSocketStrategies,
                WebsocketClientTransport.create(uri));
    }

    private RSocketRequester createRSocketRequester(RSocketRequester.Builder rSocketRequesterBuilder,
                                                    RSocketStrategies rSocketStrategies,
                                                    ClientTransport clientTransport) {
        String clientId = String.format("%s.%s", applicationName, UUID.randomUUID().toString());

        RSocketRequester rSocketRequester = rSocketRequesterBuilder
                .setupRoute("client.registration")
                .setupData(clientId)
                .rsocketStrategies(rSocketStrategies)
                .transport(clientTransport);

        rSocketRequester.rsocketClient()
                .source()
                .flatMap(RSocket::onClose)
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(consumer -> log.warn("DISCONNECTED"))
                .subscribe();

        return rSocketRequester;
    }

}
