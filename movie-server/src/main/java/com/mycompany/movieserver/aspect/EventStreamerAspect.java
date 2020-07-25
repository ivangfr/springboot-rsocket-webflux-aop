package com.mycompany.movieserver.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.movieserver.controller.dto.MovieUpdateMessage;
import com.mycompany.movieserver.model.Movie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
@Aspect
public class EventStreamerAspect {

    private final ObjectMapper objectMapper;

    private final Map<String, RSocketRequester> clients = new HashMap<>();

    public Set<String> getClientIds() {
        return clients.keySet();
    }

    @Around("execution(public * com.mycompany.movieserver.controller.MovieRSocketController.clientRegistration(..)) &&" +
            "args(rSocketRequester, clientId, ..)")
    public Object clientRegistration(ProceedingJoinPoint pjp, RSocketRequester rSocketRequester, String clientId) throws Throwable {
        long t = System.currentTimeMillis();
        log.info("=> {} :: args: {}", pjp.getSignature().toShortString(), pjp.getArgs());

        rSocketRequester.rsocket()
                .onClose()
                .doFirst(() -> {
                    clients.put(clientId, rSocketRequester);
                    log.info("Client: {} CONNECTED. Number of Clients: {}", clientId, clients.size());
                })
                .doOnError(error -> log.warn("Channel to client {} CLOSED", clientId))
                .doFinally(consumer -> {
                    clients.remove(clientId);
                    log.info("Client {} DISCONNECTED. Number of Clients: {}", clientId, clients.size());
                })
                .subscribe();

        Object retVal = pjp.proceed();

        log.info("<= {} :: Execution Time: {}ms", pjp.getSignature().toShortString(), System.currentTimeMillis() - t);
        return retVal;
    }

    @AfterReturning(pointcut = "execution(public * com.mycompany.movieserver.service.MovieService.addMovie(..))", returning = "movieMono")
    public void streamAddedMovieEvent(Mono<Movie> movieMono) {
        movieMono.subscribe(m -> clients.values()
                .forEach(rSocketRequester -> rSocketRequester.route(MOVIES_UPDATES_ROUTE)
                        .data(new MovieUpdateMessage(MovieUpdateMessage.Action.ADDED, LocalDateTime.now(), m.getImdb(), toJson(m)))
                        .send()
                        .subscribe()));
    }

    @AfterReturning(pointcut = "execution(public * com.mycompany.movieserver.service.MovieService.deleteMovie(..))", returning = "movieMono")
    public void streamDeletedMovieEvent(Mono<Movie> movieMono) {
        movieMono.subscribe(m -> clients.values()
                .forEach(rSocketRequester -> rSocketRequester.route(MOVIES_UPDATES_ROUTE)
                        .data(new MovieUpdateMessage(MovieUpdateMessage.Action.DELETED, LocalDateTime.now(), m.getImdb(), toJson(m)))
                        .send()
                        .subscribe()));
    }

    @AfterReturning(pointcut = "execution(public * com.mycompany.movieserver.service.MovieService.likeMovie(..))", returning = "movieMono")
    public void streamLikedMovieEvent(Mono<Movie> movieMono) {
        movieMono.subscribe(m -> clients.values()
                .forEach(rSocketRequester -> rSocketRequester.route(MOVIES_UPDATES_ROUTE)
                        .data(new MovieUpdateMessage(MovieUpdateMessage.Action.LIKED, LocalDateTime.now(), m.getImdb(), toJson(m)))
                        .send()
                        .subscribe()));
    }

    @AfterReturning(pointcut = "execution(public * com.mycompany.movieserver.service.MovieService.dislikeMovie(..))", returning = "movieMono")
    public void streamDislikedMovieEvent(Mono<Movie> movieMono) {
        movieMono.subscribe(m -> clients.values()
                .forEach(rSocketRequester -> rSocketRequester.route(MOVIES_UPDATES_ROUTE)
                        .data(new MovieUpdateMessage(MovieUpdateMessage.Action.DISLIKED, LocalDateTime.now(), m.getImdb(), toJson(m)))
                        .send()
                        .subscribe()));
    }

    private String toJson(Movie movie) {
        try {
            return objectMapper.writeValueAsString(movie);
        } catch (JsonProcessingException e) {
            return movie.toString();
        }
    }

    private static final String MOVIES_UPDATES_ROUTE = "movies.updates";

}
