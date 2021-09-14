package com.mycompany.movieserver.controller;

import com.mycompany.movieserver.controller.dto.AddMovieRequest;
import com.mycompany.movieserver.mapper.MovieMapper;
import com.mycompany.movieserver.model.Movie;
import com.mycompany.movieserver.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.rsocket.annotation.support.RSocketFrameTypeMessageCondition;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.function.Function;

@RequiredArgsConstructor
@Controller
public class MovieRSocketController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;

    // -- Request-Stream
    // ===================

    @MessageMapping("get.movies")
    public Flux<Movie> getMovies(@Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
                                 @Header(CONTENT_TYPE) String contentType) {
        return movieService.getMovies();
    }

    // -- Request-Response
    // =====================

    @MessageMapping("get.movie")
    public Mono<Movie> getMovie(String imdb,
                                @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
                                @Header(CONTENT_TYPE) String contentType) {
        return movieService.getMovie(imdb);
    }

    @MessageMapping("add.movie")
    public Mono<Movie> addMovie(@Valid AddMovieRequest addMovieRequest,
                                @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
                                @Header(CONTENT_TYPE) String contentType) {
        Movie movie = movieMapper.toMovie(addMovieRequest);
        return movieService.addMovie(movie);
    }

    @MessageMapping("delete.movie")
    public Mono<String> deleteMovie(String imdb,
                                    @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
                                    @Header(CONTENT_TYPE) String contentType) {
        return movieService.getMovie(imdb).flatMap(movieService::deleteMovie).map(Movie::getImdb);
    }

    // -- Fire-And-Forget
    // ====================

    @MessageMapping("like.movie")
    public void likeMovie(String imdb,
                          @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
                          @Header(CONTENT_TYPE) String contentType) {
        movieService.getMovie(imdb).flatMap(movieService::likeMovie).subscribe();
    }

    @MessageMapping("dislike.movie")
    public void dislikeMovie(String imdb,
                             @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
                             @Header(CONTENT_TYPE) String contentType) {
        movieService.getMovie(imdb).flatMap(movieService::dislikeMovie).subscribe();
    }

    // -- Channel
    // ============

    @MessageMapping("select.movies")
    public Flux<String> selectMovies(Flux<String> imdbs,
                                     @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
                                     @Header(CONTENT_TYPE) String contentType) {
        Function<Movie, String> movieFormat = m -> String.format("| IMBD: %-10s | TITLE: %-30s | LIKES: %-5s | DISLIKES: %-5s |", m.getImdb(), m.getTitle(), m.getLikes(), m.getDislikes());
        return imdbs.flatMap(movieService::getMovie).map(movieFormat);
    }

    // -- Setup
    // ==========

    @ConnectMapping("client.registration")
    public Mono<Void> clientRegistration(RSocketRequester rSocketRequester,
                                         @Payload String clientId,
                                         @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
                                         @Header(CONTENT_TYPE) String contentType) {
        return Mono.empty();
    }

    private static final String RSOCKET_FRAME_TYPE = RSocketFrameTypeMessageCondition.FRAME_TYPE_HEADER;
    private static final String CONTENT_TYPE = "contentType";
}
