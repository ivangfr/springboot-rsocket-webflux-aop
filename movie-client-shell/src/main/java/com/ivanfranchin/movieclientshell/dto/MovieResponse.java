package com.ivanfranchin.movieclientshell.dto;

import java.time.LocalDateTime;

public record MovieResponse(String imdb, String title, LocalDateTime lastModifiedDate, Integer likes, Integer dislikes) {
}
