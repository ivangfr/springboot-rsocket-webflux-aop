package com.ivanfranchin.movieserver.controller.dto;

import java.time.LocalDateTime;

public record MovieUpdateMessage(com.ivanfranchin.movieserver.controller.dto.MovieUpdateMessage.Action action,
                                 LocalDateTime timestamp, String imdb, String payload) {

    public enum Action {
        ADDED, DELETED, LIKED, DISLIKED
    }
}
