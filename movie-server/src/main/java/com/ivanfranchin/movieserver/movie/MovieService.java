package com.ivanfranchin.movieserver.movie;

import com.ivanfranchin.movieserver.movie.model.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public Mono<Movie> getMovie(String imdb) {
        return movieRepository.findById(imdb);
    }

    public Flux<Movie> getMovies() {
        return movieRepository.findAll();
    }

    public Mono<Movie> addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Mono<Movie> deleteMovie(Movie movie) {
        return movieRepository.delete(movie).then(Mono.just(movie));
    }

    public Mono<Movie> likeMovie(Movie movie) {
        movie.setLikes(movie.getLikes() + 1);
        return movieRepository.save(movie).then(Mono.just(movie));
    }

    public Mono<Movie> dislikeMovie(Movie movie) {
        movie.setDislikes(movie.getDislikes() + 1);
        return movieRepository.save(movie).then(Mono.just(movie));
    }
}
