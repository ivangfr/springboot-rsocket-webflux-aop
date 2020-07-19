package com.mycompany.movieserver.controller;

import com.mycompany.movieserver.controller.dto.AddMovieRequest;
import com.mycompany.movieserver.exception.MovieNotFoundException;
import com.mycompany.movieserver.mapper.MovieMapper;
import com.mycompany.movieserver.model.Movie;
import com.mycompany.movieserver.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MovieServerRestController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;

    @GetMapping("/movies")
    public Flux<Movie> getMovies() {
        log.info("REST :: getMovies");
        return movieService.getMovies();
    }

    @GetMapping("/movies/{imdb}")
    public Mono<Movie> getMovie(@PathVariable String imdb) {
        log.info("REST :: getMovie, imdb {}", imdb);
        return movieService.validateAndGetMovie(imdb)
                .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)));
    }

    @PostMapping("/movies")
    public Mono<Movie> addMovie(@Valid @RequestBody AddMovieRequest addMovieRequest) {
        log.info("REST :: addMovie, {}", addMovieRequest);
        Movie movie = movieMapper.toMovie(addMovieRequest);
        return movieService.addMovie(movie);
    }

    @DeleteMapping("/movies/{imdb}")
    public Mono<String> deleteMovie(@PathVariable String imdb) {
        log.info("REST :: deleteMovie, imdb {}", imdb);
        return movieService.validateAndGetMovie(imdb)
                .flatMap(movieService::deleteMovie)
                .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)));
    }

}
