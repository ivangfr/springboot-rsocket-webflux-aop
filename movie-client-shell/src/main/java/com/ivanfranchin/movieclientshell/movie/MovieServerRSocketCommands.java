package com.ivanfranchin.movieclientshell.movie;

import com.google.gson.Gson;
import com.ivanfranchin.movieclientshell.movie.dto.AddMovieRequest;
import com.ivanfranchin.movieclientshell.movie.dto.MovieResponse;
import com.ivanfranchin.movieclientshell.movie.exception.MovieNotFoundException;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Profile("rsocket-tcp || rsocket-websocket")
@Component
public class MovieServerRSocketCommands {

    private final RSocketRequester rSocketRequester;
    private final Gson gson;

    public MovieServerRSocketCommands(RSocketRequester rSocketRequester, Gson gson) {
        this.rSocketRequester = rSocketRequester;
        this.gson = gson;
    }

    @Command(name = "get-movies-rsocket", description = "Get all movies using RSocket", group = "movie-server RSocket commands")
    public String getMoviesRSocket() {
        List<String> movies = rSocketRequester.route("get.movies")
                .retrieveFlux(MovieResponse.class)
                .map(gson::toJson)
                .collectList()
                .block();
        return Objects.requireNonNull(movies).stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Command(name = "get-movie-rsocket", description = "Get movie by imdb using RSocket", group = "movie-server RSocket commands")
    public String getMovieRSocket(@Option(longName = "imdb", required = true) String imdb) {
        try {
            return rSocketRequester.route("get.movie")
                    .data(imdb)
                    .retrieveMono(MovieResponse.class)
                    .map(gson::toJson)
                    .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)))
                    .block();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Command(name = "add-movie-rsocket", description = "Add movie using RSocket", group = "movie-server RSocket commands")
    public String addMovieRSocket(@Option(longName = "imdb", required = true) String imdb,
                                  @Option(longName = "title", required = true) String title) {
        AddMovieRequest addMovieRequest = new AddMovieRequest(imdb, title);
        return rSocketRequester.route("add.movie")
                .data(addMovieRequest)
                .retrieveMono(MovieResponse.class)
                .map(gson::toJson)
                .block();
    }

    @Command(name = "delete-movie-rsocket", description = "Delete movie using RSocket", group = "movie-server RSocket commands")
    public String deleteMovieRSocket(@Option(longName = "imdb", required = true) String imdb) {
        try {
            return rSocketRequester.route("delete.movie")
                    .data(imdb)
                    .retrieveMono(String.class)
                    .map(gson::toJson)
                    .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)))
                    .block();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Command(name = "like-movie-rsocket", description = "Like movie using RSocket", group = "movie-server RSocket commands")
    public String likeMovieRSocket(@Option(longName = "imdb", required = true) String imdb) {
        rSocketRequester.route("like.movie")
                .data(imdb)
                .send()
                .block();
        return "Like submitted";
    }

    @Command(name = "dislike-movie-rsocket", description = "Dislike movie using RSocket", group = "movie-server RSocket commands")
    public String dislikeMovieRSocket(@Option(longName = "imdb", required = true) String imdb) {
        rSocketRequester.route("dislike.movie")
                .data(imdb)
                .send()
                .block();
        return "Dislike submitted";
    }

    @Command(name = "select-movies-rsocket", description = "Select some movies using RSocket. Inform the imdb of the movies separated by comma", group = "movie-server RSocket commands")
    public String selectMoviesRSocket(@Option(longName = "imdbs", required = true) String imdbs) {
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
