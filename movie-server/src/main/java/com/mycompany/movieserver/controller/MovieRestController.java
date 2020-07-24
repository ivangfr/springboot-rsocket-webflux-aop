package com.mycompany.movieserver.controller;

import com.mycompany.movieserver.controller.dto.AddMovieRequest;
import com.mycompany.movieserver.exception.MovieNotFoundException;
import com.mycompany.movieserver.mapper.MovieMapper;
import com.mycompany.movieserver.model.Movie;
import com.mycompany.movieserver.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MovieRestController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;

    @GetMapping(value = "/movies", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Movie> getMovies() {
        return movieService.getMovies();
    }

    @GetMapping("/movies/{imdb}")
    public Mono<Movie> getMovie(@PathVariable String imdb) {
        return movieService.getMovie(imdb).switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/movies")
    public Mono<Movie> addMovie(@Valid @RequestBody AddMovieRequest addMovieRequest) {
        Movie movie = movieMapper.toMovie(addMovieRequest);
        return movieService.addMovie(movie);
    }

    @DeleteMapping("/movies/{imdb}")
    public Mono<String> deleteMovie(@PathVariable String imdb) {
        return movieService.getMovie(imdb)
                .flatMap(movieService::deleteMovie)
                .map(Movie::getImdb)
                .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)));
    }

    @PatchMapping("/movies/{imdb}/like")
    public void likeMovie(@PathVariable String imdb) {
        movieService.getMovie(imdb).flatMap(movieService::likeMovie).subscribe();
    }

    @PatchMapping("/movies/{imdb}/dislike")
    public void dislikeMovie(@PathVariable String imdb) {
        movieService.getMovie(imdb).flatMap(movieService::dislikeMovie).subscribe();
    }

}
