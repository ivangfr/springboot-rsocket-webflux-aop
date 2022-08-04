package com.ivanfranchin.movieclientui.controller.dto;

import java.time.LocalDateTime;

public record MovieUpdateMessage(String action, LocalDateTime timestamp, String imdb, String payload) {
}
