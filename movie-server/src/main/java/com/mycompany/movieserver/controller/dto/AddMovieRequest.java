package com.mycompany.movieserver.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AddMovieRequest {

    @NotBlank
    private String imdb;

    @Size(min = 1, max = 30)
    private String title;

}
