package com.ivanfranchin.movieclientui.config;

import com.ivanfranchin.movieclientui.controller.MovieClientUiController;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Configuration
public class MovieServerRSocketConfig {

    private static final Logger log = LoggerFactory.getLogger(MovieServerRSocketConfig.class);

    private final MovieClientUiController movieClientUiController;

    public MovieServerRSocketConfig(MovieClientUiController movieClientUiController) {
        this.movieClientUiController = movieClientUiController;
    }

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
        String clientId = String.format("%s.%s", applicationName, UUID.randomUUID());
        log.info("Connecting using client ID: {}", clientId);

        SocketAcceptor socketAcceptor = RSocketMessageHandler.responder(rSocketStrategies, movieClientUiController);

        RetryBackoffSpec retryBackoffSpec = Retry.fixedDelay(120, Duration.ofSeconds(1))
                .doBeforeRetry(retrySignal -> log.warn("Reconnecting... {}", retrySignal));

        RSocketRequester rSocketRequester = rSocketRequesterBuilder
                .setupRoute("client.registration")
                .setupData(clientId)
                .rsocketStrategies(rSocketStrategies)
                .rsocketConnector(connector -> connector.acceptor(socketAcceptor).reconnect(retryBackoffSpec))
                .transport(clientTransport);

        rSocketRequester.rsocketClient()
                .source()
                .flatMap(RSocket::onClose)
                .repeat()
                .retryWhen(retryBackoffSpec)
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(consumer -> log.info("DISCONNECTED"))
                .subscribe();

        return rSocketRequester;
    }
}
