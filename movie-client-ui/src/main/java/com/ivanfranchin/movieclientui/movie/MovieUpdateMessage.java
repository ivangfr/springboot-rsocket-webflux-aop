package com.ivanfranchin.movieclientui.movie;

import java.time.LocalDateTime;

public record MovieUpdateMessage(String action, LocalDateTime timestamp, String imdb, String payload) {
}
