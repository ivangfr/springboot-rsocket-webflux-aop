package com.mycompany.movieclientui.controller.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovieUpdateMessage {

    private String action;
    private LocalDateTime timestamp;
    private String imdb;
    private String payload;
}
