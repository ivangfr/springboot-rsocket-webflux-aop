package com.mycompany.movieclient.command;

import com.google.gson.Gson;
import com.mycompany.movieclient.dto.AddMovieRequest;
import com.mycompany.movieclient.dto.MovieDto;
import com.mycompany.movieclient.exception.MovieNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@ShellComponent
@ShellCommandGroup("movie-server RSocket commands")
public class MovieServerRSocketCommands {

    private final RSocketRequester rSocketRequester;
    private final Gson gson;

    @ShellMethod(key = "get-movies-rsocket", value = "Get all movies using RSocket")
    public List<String> getMoviesRSocket() {
        return rSocketRequester.route("get-movies")
                .retrieveFlux(MovieDto.class)
                .map(gson::toJson)
                .collectList()
                .block();
    }

    @ShellMethod(key = "get-movie-rsocket", value = "Get movie by imdb using RSocket")
    public String getMovieRSocket(String imdb) {
        return rSocketRequester.route("get-movie")
                .data(imdb)
                .retrieveMono(MovieDto.class)
                .map(gson::toJson)
                .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)))
                .block();
    }

    @ShellMethod(key = "add-movie-rsocket", value = "Add movie using RSocket")
    public String addMovieRSocket(String imdb, String title) {
        AddMovieRequest addMovieRequest = new AddMovieRequest(imdb, title);
        return rSocketRequester.route("add-movie")
                .data(addMovieRequest)
                .retrieveMono(MovieDto.class)
                .map(gson::toJson)
                .block();
    }

    @ShellMethod(key = "delete-movie-rsocket", value = "Delete movie using RSocket")
    public String deleteMovieRSocket(String imdb) {
        return rSocketRequester.route("delete-movie")
                .data(imdb)
                .retrieveMono(String.class)
                .map(gson::toJson)
                .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)))
                .block();
    }

}
