package com.mycompany.movieserver.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddMovieRequest {

    @NotBlank
    private String imdb;

    @NotBlank
    private String title;

}
