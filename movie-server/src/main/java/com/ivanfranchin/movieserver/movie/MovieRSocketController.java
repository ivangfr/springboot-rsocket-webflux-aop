package com.ivanfranchin.movieserver.movie;

import com.ivanfranchin.movieserver.movie.dto.AddMovieRequest;
import com.ivanfranchin.movieserver.movie.model.Movie;
import jakarta.validation.Valid;
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

@RequiredArgsConstructor
@Controller
public class MovieRSocketController {

    private final MovieService movieService;

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
        return movieService.addMovie(Movie.from(addMovieRequest));
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
        return imdbs.flatMap(movieService::getMovie)
                .map(movie ->
                        String.format("| IMDB: %-10s | TITLE: %-30s | LIKES: %-5s | DISLIKES: %-5s |",
                                movie.getImdb(), movie.getTitle(), movie.getLikes(), movie.getDislikes()));
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
