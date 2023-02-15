package com.ivanfranchin.movieserver.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddMovieRequest(@NotBlank String imdb, @Size(min = 1, max = 30) String title) {
}
