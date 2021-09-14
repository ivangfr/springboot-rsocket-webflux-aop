package com.mycompany.movieserver.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MovieUpdateMessage {

    private Action action;
    private LocalDateTime timestamp;
    private String imdb;
    private String payload;

    public enum Action {
        ADDED, DELETED, LIKED, DISLIKED
    }
}
