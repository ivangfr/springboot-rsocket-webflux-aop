package com.mycompany.movieclient.rest;

import com.mycompany.movieclient.rest.dto.CreateMovieRequest;
import com.mycompany.movieclient.rest.dto.MovieDto;
import com.mycompany.movieclient.exception.MovieNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class MovieClientController {

    private final RSocketRequester rSocket;

    @GetMapping("/movies")
    public Flux<MovieDto> getMovies() {
        return rSocket.route("get-movies").retrieveFlux(MovieDto.class);
    }

    @GetMapping("/movies/{imdb}")
    public Mono<MovieDto> getMovie(@PathVariable String imdb) {
        return rSocket.route("get-movie")
                .data(imdb)
                .retrieveMono(MovieDto.class)
                .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)));
    }

    @PostMapping("/movies")
    public Mono<MovieDto> createMovie(@Valid @RequestBody CreateMovieRequest createMovieRequest) {
        return rSocket.route("create-movie")
                .data(createMovieRequest)
                .retrieveMono(MovieDto.class);
    }

    @DeleteMapping("/movies/{imdb}")
    public Mono<String> deleteMovie(@PathVariable String imdb) {
        return rSocket.route("delete-movie")
                .data(imdb)
                .retrieveMono(String.class)
                .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)));
    }

}
