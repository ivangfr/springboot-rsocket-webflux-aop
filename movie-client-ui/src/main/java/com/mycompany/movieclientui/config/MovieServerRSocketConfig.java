package com.mycompany.movieclientui.config;

import com.mycompany.movieclientui.controller.MovieClientUiController;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class MovieServerRSocketConfig {

    private final MovieClientUiController movieClientUiController;

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
        log.info("Connecting using client ID: {}", clientId);

        SocketAcceptor socketAcceptor = RSocketMessageHandler.responder(rSocketStrategies, movieClientUiController);

        RSocketRequester rSocketRequester = rSocketRequesterBuilder
                .setupRoute("client.registration")
                .setupData(clientId)
                .rsocketStrategies(rSocketStrategies)
                .rsocketConnector(connector -> connector.acceptor(socketAcceptor))
                .connect(clientTransport)
                .block();

        rSocketRequester.rsocket()
                .onClose()
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(consumer -> log.info("DISCONNECTED"))
                .subscribe();

        return rSocketRequester;
    }

}
