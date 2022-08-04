package com.ivanfranchin.movieserver.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record AddMovieRequest(@NotBlank String imdb, @Size(min = 1, max = 30) String title) {
}
