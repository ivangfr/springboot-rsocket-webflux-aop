package com.ivanfranchin.movieclientshell.movie;

import com.google.gson.Gson;
import com.ivanfranchin.movieclientshell.movie.dto.AddMovieRequest;
import com.ivanfranchin.movieclientshell.movie.dto.MovieResponse;
import org.springframework.http.MediaType;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MovieServerRestCommands {

    private final WebClient webClient;
    private final Gson gson;

    public MovieServerRestCommands(WebClient webClient, Gson gson) {
        this.webClient = webClient;
        this.gson = gson;
    }

    @Command(name = "get-movies-rest", description = "Get all movies using REST", group = "movie-server REST commands")
    public String getMoviesRest() {
        try {
            List<String> movies = webClient.get()
                    .retrieve()
                    .bodyToFlux(MovieResponse.class)
                    .map(gson::toJson)
                    .collectList()
                    .block();
            return Objects.requireNonNull(movies).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Command(name = "get-movie-rest", description = "Get movie by imdb using REST", group = "movie-server REST commands")
    public String getMovieRest(@Option(longName = "imdb", required = true) String imdb) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/{imdb}").build(imdb))
                    .retrieve()
                    .bodyToMono(MovieResponse.class)
                    .map(gson::toJson)
                    .block();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Command(name = "add-movie-rest", description = "Add movie using REST", group = "movie-server REST commands")
    public String addMovieRest(@Option(longName = "imdb", required = true) String imdb,
                               @Option(longName = "title", required = true) String title) {
        try {
            AddMovieRequest addMovieRequest = new AddMovieRequest(imdb, title);
            return webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(addMovieRequest)
                    .retrieve()
                    .bodyToMono(MovieResponse.class)
                    .map(gson::toJson)
                    .block();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Command(name = "delete-movie-rest", description = "Delete movie using REST", group = "movie-server REST commands")
    public String deleteMovieRest(@Option(longName = "imdb", required = true) String imdb) {
        try {
            return webClient.delete()
                    .uri(uriBuilder -> uriBuilder.path("/{imdb}").build(imdb))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(gson::toJson)
                    .block();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Command(name = "like-movie-rest", description = "Like movie using REST", group = "movie-server REST commands")
    public String likeMovieRest(@Option(longName = "imdb", required = true) String imdb) {
        try {
            webClient.patch()
                    .uri(uriBuilder -> uriBuilder.path("/{imdb}/like").build(imdb))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return "Like submitted";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Command(name = "dislike-movie-rest", description = "Dislike movie using REST", group = "movie-server REST commands")
    public String dislikeMovieRest(@Option(longName = "imdb", required = true) String imdb) {
        try {
            webClient.patch()
                    .uri(uriBuilder -> uriBuilder.path("/{imdb}/dislike").build(imdb))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return "Dislike submitted";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
