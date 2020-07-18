package com.mycompany.movieserver.service;

import com.mycompany.movieserver.model.Movie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {

    Mono<Movie> validateAndGetMovie(String imdb);

    Flux<Movie> getMovies();

    Mono<Movie> createMovie(Movie movie);

    Mono<String> deleteMovie(Movie movie);

}
