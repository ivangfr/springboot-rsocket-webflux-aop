package com.mycompany.movieserver.controller.dto;

import lombok.Data;

@Data
public class CreateMovieRequest {

    private String imdb;
    private String title;

}
