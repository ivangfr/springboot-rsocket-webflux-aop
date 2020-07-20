package com.mycompany.movieserver.service;

import com.mycompany.movieserver.model.Movie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {

    Mono<Movie> getMovie(String imdb);

    Flux<Movie> getMovies();

    Mono<Movie> addMovie(Movie movie);

    Mono<String> deleteMovie(Movie movie);

    Mono<Void> likeMovie(Movie movie);

    Mono<Void> dislikeMovie(Movie movie);

}