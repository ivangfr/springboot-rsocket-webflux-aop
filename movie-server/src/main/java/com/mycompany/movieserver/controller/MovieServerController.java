package com.mycompany.movieserver.controller;

import com.mycompany.movieserver.controller.dto.CreateMovieRequest;
import com.mycompany.movieserver.mapper.MovieMapper;
import com.mycompany.movieserver.model.Movie;
import com.mycompany.movieserver.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
public class MovieServerController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;

    @MessageMapping("get-movies")
    public Flux<Movie> getMovies() {
        return movieService.getMovies();
    }

    @MessageMapping("get-movie")
    public Mono<Movie> getMovies(String imdb) {
        return movieService.validateAndGetMovie(imdb);
    }

    @MessageMapping("create-movie")
    public Mono<Movie> createMovie(CreateMovieRequest createMovieRequest) {
        Movie movie = movieMapper.toMovie(createMovieRequest);
        return movieService.createMovie(movie);
    }

    @MessageMapping("delete-movie")
    public Mono<String> deleteMovie(String imdb) {
        return movieService.validateAndGetMovie(imdb).flatMap(movieService::deleteMovie);
    }

}
