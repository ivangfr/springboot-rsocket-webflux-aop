package com.ivanfranchin.movieclientshell.movie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MovieServerWebClientConfig {

    @Bean
    WebClient webClient(@Value("${movie-server.host:localhost}") String movieServerHost,
                        @Value("${movie-server.rest.port:8080}") int movieServerRestPort) {
        return WebClient.create(String.format("http://%s:%s/api/movies", movieServerHost, movieServerRestPort));
    }
}
