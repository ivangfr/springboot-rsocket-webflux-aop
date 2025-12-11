package com.ivanfranchin.movieclientshell.movie;

import com.google.gson.Gson;
import com.ivanfranchin.movieclientshell.movie.dto.AddMovieRequest;
import com.ivanfranchin.movieclientshell.movie.dto.MovieResponse;
import com.ivanfranchin.movieclientshell.movie.exception.MovieNotFoundException;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Profile("rsocket-tcp || rsocket-websocket")
@ShellComponent
@ShellCommandGroup("movie-server RSocket commands")
public class MovieServerRSocketCommands {

    private final RSocketRequester rSocketRequester;
    private final Gson gson;

    public MovieServerRSocketCommands(RSocketRequester rSocketRequester, Gson gson) {
        this.rSocketRequester = rSocketRequester;
        this.gson = gson;
    }

    @ShellMethod(key = "get-movies-rsocket", value = "Get all movies using RSocket")
    public String getMoviesRSocket() {
        List<String> movies = rSocketRequester.route("get.movies")
                .retrieveFlux(MovieResponse.class)
                .map(gson::toJson)
                .collectList()
                .block();
        return Objects.requireNonNull(movies).stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(key = "get-movie-rsocket", value = "Get movie by imdb using RSocket")
    public String getMovieRSocket(String imdb) {
        return rSocketRequester.route("get.movie")
                .data(imdb)
                .retrieveMono(MovieResponse.class)
                .map(gson::toJson)
                .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)))
                .block();
    }

    @ShellMethod(key = "add-movie-rsocket", value = "Add movie using RSocket")
    public String addMovieRSocket(String imdb, String title) {
        AddMovieRequest addMovieRequest = new AddMovieRequest(imdb, title);
        return rSocketRequester.route("add.movie")
                .data(addMovieRequest)
                .retrieveMono(MovieResponse.class)
                .map(gson::toJson)
                .block();
    }

    @ShellMethod(key = "delete-movie-rsocket", value = "Delete movie using RSocket")
    public String deleteMovieRSocket(String imdb) {
        return rSocketRequester.route("delete.movie")
                .data(imdb)
                .retrieveMono(String.class)
                .map(gson::toJson)
                .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)))
                .block();
    }

    @ShellMethod(key = "like-movie-rsocket", value = "Like movie using RSocket")
    public String likeMovieRSocket(String imdb) {
        rSocketRequester.route("like.movie")
                .data(imdb)
                .send()
                .block();
        return "Like submitted";
    }

    @ShellMethod(key = "dislike-movie-rsocket", value = "Dislike movie using RSocket")
    public String dislikeMovieRSocket(String imdb) {
        rSocketRequester.route("dislike.movie")
                .data(imdb)
                .send()
                .block();
        return "Dislike submitted";
    }

    @ShellMethod(key = "select-movies-rsocket", value = "Select some movies using RSocket. Inform the imdb of the movies separated by comma")
    public String selectMoviesRSocket(String imdbs) {
        Flux<String> imdbFlux = Flux.just(imdbs.split(","));
        List<String> movies = rSocketRequester.route("select.movies")
                .data(imdbFlux)
                .retrieveFlux(String.class)
                .collectList()
                .block();
        return Objects.requireNonNull(movies).stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
