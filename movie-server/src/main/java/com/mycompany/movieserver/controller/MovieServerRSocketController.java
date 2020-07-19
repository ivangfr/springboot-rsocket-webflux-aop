package com.mycompany.movieserver.controller;

import com.mycompany.movieserver.controller.dto.AddMovieRequest;
import com.mycompany.movieserver.mapper.MovieMapper;
import com.mycompany.movieserver.model.Movie;
import com.mycompany.movieserver.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MovieServerRSocketController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;

    @MessageMapping("get-movies")
    public Flux<Movie> getMovies() {
        log.info("RSocket :: getMovies");
        return movieService.getMovies();
    }

    @MessageMapping("get-movie")
    public Mono<Movie> getMovie(String imdb) {
        log.info("RSocket :: getMovie, {}", imdb);
        return movieService.validateAndGetMovie(imdb);
    }

    @MessageMapping("add-movie")
    public Mono<Movie> addMovie(AddMovieRequest addMovieRequest) {
        log.info("RSocket :: addMovie, {}", addMovieRequest);
        Movie movie = movieMapper.toMovie(addMovieRequest);
        return movieService.addMovie(movie);
    }

    @MessageMapping("delete-movie")
    public Mono<String> deleteMovie(String imdb) {
        log.info("RSocket :: deleteMovie, {}", imdb);
        return movieService.validateAndGetMovie(imdb).flatMap(movieService::deleteMovie);
    }

}
