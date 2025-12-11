package com.ivanfranchin.movieclientshell.movie;

import com.google.gson.Gson;
import com.ivanfranchin.movieclientshell.movie.dto.AddMovieRequest;
import com.ivanfranchin.movieclientshell.movie.dto.MovieResponse;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("movie-server REST commands")
public class MovieServerRestCommands {

    private final WebClient webClient;
    private final Gson gson;

    public MovieServerRestCommands(WebClient webClient, Gson gson) {
        this.webClient = webClient;
        this.gson = gson;
    }

    @ShellMethod(key = "get-movies-rest", value = "Get all movies using REST")
    public String getMoviesRest() {
        List<String> movies = webClient.get()
                .retrieve()
                .bodyToFlux(MovieResponse.class)
                .map(gson::toJson)
                .collectList()
                .block();
        return Objects.requireNonNull(movies).stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(key = "get-movie-rest", value = "Get movie by imdb using REST")
    public String getMovieRest(String imdb) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{imdb}").build(imdb))
                .retrieve()
                .bodyToMono(MovieResponse.class)
                .map(gson::toJson)
                .block();
    }

    @ShellMethod(key = "add-movie-rest", value = "Add movie using REST")
    public String addMovieRest(String imdb, String title) {
        AddMovieRequest addMovieRequest = new AddMovieRequest(imdb, title);
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addMovieRequest)
                .retrieve()
                .bodyToMono(MovieResponse.class)
                .map(gson::toJson)
                .block();
    }

    @ShellMethod(key = "delete-movie-rest", value = "Delete movie using REST")
    public String deleteMovieRest(String imdb) {
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/{imdb}").build(imdb))
                .retrieve()
                .bodyToMono(String.class)
                .map(gson::toJson)
                .block();
    }

    @ShellMethod(key = "like-movie-rest", value = "Like movie using REST")
    public String likeMovieRest(String imdb) {
        webClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/{imdb}/like").build(imdb))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        return "Like submitted";
    }

    @ShellMethod(key = "dislike-movie-rest", value = "Dislike movie using REST")
    public String dislikeMovieRest(String imdb) {
        webClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/{imdb}/dislike").build(imdb))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        return "Dislike submitted";
    }
}
