package com.ivanfranchin.movieserver.service;

import com.ivanfranchin.movieserver.model.Movie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {

    Mono<Movie> getMovie(String imdb);

    Flux<Movie> getMovies();

    Mono<Movie> addMovie(Movie movie);

    Mono<Movie> deleteMovie(Movie movie);

    Mono<Movie> likeMovie(Movie movie);

    Mono<Movie> dislikeMovie(Movie movie);
}
