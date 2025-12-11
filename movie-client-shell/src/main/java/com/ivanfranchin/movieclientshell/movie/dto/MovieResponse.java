package com.ivanfranchin.movieclientshell.movie.dto;

import java.time.LocalDateTime;

public record MovieResponse(String imdb, String title, LocalDateTime lastModifiedDate, Integer likes, Integer dislikes) {
}
